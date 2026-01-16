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
            // Lavaplayer usually provides 48000Hz, 16-bit, stereo, signed, big-endian
            AudioFormat audioFormat = new AudioFormat(
                    format.sampleRate,
                    16,
                    format.channelCount,
                    true, // signed
                    true  // big-endian
            );

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            boolean performByteSwap = false;

            if (!AudioSystem.isLineSupported(info)) {
                // Try requesting Little Endian
                AudioFormat littleEndianFormat = new AudioFormat(
                        format.sampleRate,
                        16,
                        format.channelCount,
                        true, // signed
                        false // little-endian
                );
                DataLine.Info littleEndianInfo = new DataLine.Info(SourceDataLine.class, littleEndianFormat);

                if (AudioSystem.isLineSupported(littleEndianInfo)) {
                    log.info("Big-endian audio not supported, falling back to little-endian with software conversion.");
                    audioFormat = littleEndianFormat;
                    info = littleEndianInfo;
                    performByteSwap = true;
                } else {
                    log.warn("Audio line not supported (No audio device found?). Playback will be simulated (silent).");
                    // We consume frames anyway to drain the player
                    consumeFramesWithoutOutput();
                    return;
                }
            }

            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
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

                        // We are about to consume 'data'. If we need to swap bytes, we should do it now.
                        // Since 'data' comes from Lavaplayer's pool, we can modify it in place if we are sure
                        // we are the only consumer. To be safe/clean, we swap in place.
                        if (performByteSwap) {
                            for (int i = 0; i < length - 1; i += 2) {
                                byte temp = data[i];
                                data[i] = data[i+1];
                                data[i+1] = temp;
                            }
                        }

                        // Handle leftover bytes from previous frame
                        if (leftoverCount > 0) {
                            int needed = frameSize - leftoverCount;
                            if (length >= needed) {
                                System.arraycopy(data, offset, leftoverBuffer, leftoverCount, needed);
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

                        if (length > 0) {
                            int writeLength = (length / frameSize) * frameSize;
                            if (writeLength > 0) {
                                line.write(data, offset, writeLength);
                                offset += writeLength;
                                length -= writeLength;
                            }

                            // Store remaining bytes that didn't form a full frame
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
