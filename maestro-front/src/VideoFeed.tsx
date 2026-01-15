export default function VideoFeed() {
    const reset = () => {
        localStorage.removeItem('maestro_user_profile');
        window.location.reload();
    };

    const videoFeedUrl : string = import.meta.env.VITE_VIDEO_URL ?? "http://localhost:5000/video_feed";

    return (
        <div className="video-layout">
            <h1>Maestro Vision Feed</h1>

            <div className="video-container">
                {/* URL du flux backend */}
                <img src={videoFeedUrl} alt="Live Video Feed" />
            </div>

            <p className="instruction">Pinch thumb and index finger to trigger the Music Agent.</p>

            <button onClick={reset} className="btn-reset">
                Reset Profile (Dev Mode)
            </button>
        </div>
    );
}