package com.arcos.maestromvp.Audio;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

@Slf4j
public class LocalAudioOutput implements Runnable {
    private final AudioPlayer audioPlayer;
    private final AudioDataFormat format;
    private SourceDataLine line;
    private volatile boolean running = true;

    public LocalAudioOutput(AudioPlayer audioPlayer, AudioDataFormat format) {
        this.audioPlayer = audioPlayer;
        this.format = format;
    }

    @Override
    public void run() {
        try {
            // Lavaplayer provides signed 16-bit Big-Endian PCM
            // We PREFER Little Endian for better compatibility with PC hardware (x86/ARM),
            // but we can fall back to Big Endian if needed.
            AudioFormat targetFormat;
            boolean performByteSwap = false;

            // 1. Try Little Endian first
            AudioFormat littleEndianFormat = new AudioFormat(
                    format.sampleRate,
                    16,
                    format.channelCount,
                    true, // signed
                    false // little-endian
            );
            DataLine.Info leInfo = new DataLine.Info(SourceDataLine.class, littleEndianFormat);

            if (AudioSystem.isLineSupported(leInfo)) {
                targetFormat = littleEndianFormat;
                performByteSwap = true; // Lavaplayer (BE) -> Line (LE)
                log.info("Using Little-Endian audio format (with software conversion).");
            } else {
                // 2. Try Big Endian
                AudioFormat bigEndianFormat = new AudioFormat(
                        format.sampleRate,
                        16,
                        format.channelCount,
                        true, // signed
                        true  // big-endian
                );
                DataLine.Info beInfo = new DataLine.Info(SourceDataLine.class, bigEndianFormat);

                if (AudioSystem.isLineSupported(beInfo)) {
                    targetFormat = bigEndianFormat;
                    performByteSwap = false; // Lavaplayer (BE) -> Line (BE)
                    log.info("Using Big-Endian audio format (native).");
                } else {
                    log.warn("Audio line not supported (No audio device found?). Playback will be simulated (silent).");
                    consumeFramesWithoutOutput();
                    return;
                }
            }

            line = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, targetFormat));
            line.open(targetFormat);
            line.start();
            log.info("Audio line started successfully.");

            long frameDuration = format.frameDuration();
            int frameSize = line.getFormat().getFrameSize();
            if (frameSize == -1) {
                frameSize = 4; // Default fallback if unknown, assuming stereo 16-bit
            }
            byte[] leftoverBuffer = new byte[frameSize];
            int leftoverCount = 0;

            while (running) {
                AudioFrame frame = audioPlayer.provide();

                if (frame != null) {
                    try {
                        byte[] data = frame.getData();
                        int offset = 0;
                        int length = frame.getDataLength();

                        // Handle leftover bytes from previous frame
                        if (leftoverCount > 0) {
                            int needed = frameSize - leftoverCount;
                            if (length >= needed) {
                                System.arraycopy(data, offset, leftoverBuffer, leftoverCount, needed);

                                if (performByteSwap) {
                                    for (int i = 0; i < frameSize; i += 2) {
                                        byte temp = leftoverBuffer[i];
                                        leftoverBuffer[i] = leftoverBuffer[i+1];
                                        leftoverBuffer[i+1] = temp;
                                    }
                                }
                                line.write(leftoverBuffer, 0, frameSize);

                                leftoverCount = 0;
                                offset += needed;
                                length -= needed;
                            } else {
                                System.arraycopy(data, offset, leftoverBuffer, leftoverCount, length);
                                leftoverCount += length;
                                length = 0;
                            }
                        }

                        // Write bulk data
                        if (length > 0) {
                            int writeLength = (length / frameSize) * frameSize;
                            if (writeLength > 0) {
                                if (performByteSwap) {
                                    // Swap in place for the valid chunk only
                                    for (int i = offset; i < offset + writeLength; i += 2) {
                                        byte temp = data[i];
                                        data[i] = data[i+1];
                                        data[i+1] = temp;
                                    }
                                }
                                line.write(data, offset, writeLength);
                                offset += writeLength;
                                length -= writeLength;
                            }

                            // Store remaining bytes
                            if (length > 0) {
                                System.arraycopy(data, offset, leftoverBuffer, leftoverCount, length);
                                leftoverCount += length;
                            }
                        }
                    } catch (Exception e) {
                        log.error("Error writing to audio line", e);
                    }
                } else {
                    try {
                        Thread.sleep(frameDuration);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            log.error("Error initializing or running local audio output", e);
        } finally {
            if (line != null) {
                line.drain();
                line.close();
            }
        }
    }

    private void consumeFramesWithoutOutput() {
        long frameDuration = format.frameDuration();
        while (running) {
            AudioFrame frame = audioPlayer.provide();
            if (frame == null) {
                try {
                    Thread.sleep(frameDuration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public void stop() {
        running = false;
    }
}
