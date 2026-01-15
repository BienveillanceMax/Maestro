interface ImportMetaEnv {
    readonly VITE_VIDEO_URL: string;
    readonly VITE_USER_PROFILE_URL: string;
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}
