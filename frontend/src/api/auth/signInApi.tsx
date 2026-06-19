import { type SignInRequest } from "../../types/auth";
import { API_CONFIG } from "../../config/api.config";


export const signInApi = async (data: SignInRequest): Promise<string> => {
    const response = await fetch(`${API_CONFIG.BASE_URL}/auth/signIn`, {
        method: 'POST',
        headers: {
        'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    });

    const text = await response.text();

    if(!response.ok){
        throw new Error(text);
    }

    return text;
}

