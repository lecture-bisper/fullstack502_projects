import {create} from "zustand/react";

const useUserStore = create((set) => ({
    user: null,
    setUser: (userData) => set({user: userData}),
    clearUser: () => set({user: null}),
}))

export default useUserStore;