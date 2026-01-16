export interface UserProfileDto {
    likedGenres: string[];
    hatedGenres: string[];
    openToDiscovery: boolean;
    additionnalInformation: string; // "additionnal" avec 2 'n' pour matcher ton Java
}

export const GENRES = [
    "Rock", "Classic Rock", "Hard Rock", "Metal", "Pop", "K-Pop", "Hip Hop",
    "Rap", "R&B", "Jazz", "Blues", "Electronic", "Techno", "House", "Country",
    "Folk", "Reggae", "Latin", "Classical", "Lo-Fi"
];

declare global {
    interface ImportMetaEnv {
        readonly VITE_VIDEO_URL: string;
        readonly VITE_USER_PROFILE_URL: string;
    }
}

export {};