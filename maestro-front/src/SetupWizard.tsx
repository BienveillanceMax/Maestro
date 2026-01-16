import { useState } from 'react';
import { GENRES, type UserProfileDto } from './types';
import './App.css'; // On réutilise le CSS global pour simplifier

interface Props {
    onComplete: (profile: UserProfileDto) => void;
}

export default function SetupWizard({ onComplete }: Props) {
    const [liked, setLiked] = useState<string[]>([]);
    const [hated, setHated] = useState<string[]>([]);
    const [discovery, setDiscovery] = useState(false);
    const [info, setInfo] = useState("");

    const toggleGenre = (genre: string, list: string[], setList: React.Dispatch<React.SetStateAction<string[]>>) => {
        if (list.includes(genre)) {
            setList(list.filter(g => g !== genre));
        } else {
            setList([...list, genre]);
        }
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onComplete({
            likedGenres: liked,
            hatedGenres: hated,
            openToDiscovery: discovery,
            additionnalInformation: info
        });
    };

    return (
        <div className="wizard-container">
        <div className="card">
            <h2>Welcome to Maestro !</h2>
    <form onSubmit={handleSubmit}>

        {/* 1. Discovery */}
        <section>
        <h3>1. Discovery</h3>
    <label className="checkbox-row">
    <input
        type="checkbox"
    checked={discovery}
    onChange={e => setDiscovery(e.target.checked)}
    />
    <span>I am open to discovering new music</span>
    </label>
    </section>

    {/* 2. Liked */}
    <section>
        <h3>2. Select Genres You LIKE</h3>
    <div className="grid">
        {GENRES.map(genre => (
                <label key={`like-${genre}`} className={`chip ${liked.includes(genre) ? 'active' : ''}`}>
    <input
        type="checkbox"
    checked={liked.includes(genre)}
    onChange={() => toggleGenre(genre, liked, setLiked)}
    hidden
    />
    {liked.includes(genre) ? "★ " : ""}{genre}
    </label>
))}
    </div>
    </section>

    {/* 3. Hated */}
    <section>
        <h3>3. Select Genres You HATE</h3>
    <p className="subtitle">(Genres you liked are disabled)</p>
    <div className="grid">
        {GENRES.map(genre => {
                const isDisabled = liked.includes(genre);
                return (
                    <label
                        key={`hate-${genre}`}
                className={`chip ${hated.includes(genre) ? 'active-hate' : ''} ${isDisabled ? 'disabled' : ''}`}
            >
                <input
                    type="checkbox"
                checked={hated.includes(genre)}
                onChange={() => toggleGenre(genre, hated, setHated)}
                disabled={isDisabled}
                hidden
                />
                {hated.includes(genre) ? "✖ " : ""}{genre}
                </label>
            )
            })}
        </div>
        </section>

    {/* 4. Additional Info */}
    <section>
        <h3>4. Anything else?</h3>
        <textarea
        value={info}
    onChange={e => setInfo(e.target.value)}
    placeholder="Tell us about your specific taste..."
    rows={3}
    />
    </section>

    <button type="submit" className="btn-primary">Save Profile & Enter</button>
    </form>
    </div>
    </div>
);
}