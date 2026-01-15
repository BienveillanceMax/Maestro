import { useState } from 'react';
import SetupWizard from './SetupWizard';
import VideoFeed from './VideoFeed';
import type {UserProfileDto} from './types';
import './App.css';
import axios from "axios";

function App() {
    // CORRECTION: On initialise l'état directement en lisant le localStorage via une fonction.
    // Cela évite le double rendu et l'erreur ESLint.
    const [hasProfile, setHasProfile] = useState<boolean>(() => {
        const saved = localStorage.getItem('maestro_user_profile');
        return !!saved; // Renvoie true si le profil existe, sinon false
    });

    const back_url : string = import.meta.env.VITE_USER_PROFILE_URL;

    const handleProfileComplete = (profile: UserProfileDto) => {
        // 1. Sauvegarde locale
        localStorage.setItem('maestro_user_profile', JSON.stringify(profile));

        console.log("Envoi au backend:", profile);
        axios.post(back_url, profile).then(() => setHasProfile(true));
    };

    // Plus besoin de "if (loading) return null;" car l'état est connu dès le premier cycle.

    return (
        <main>
            {hasProfile ? <VideoFeed /> : <SetupWizard onComplete={handleProfileComplete} />}
        </main>
    );
}

export default App;