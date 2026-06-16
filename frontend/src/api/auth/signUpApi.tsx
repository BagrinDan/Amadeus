import { type SignUpRequest } from "../../types/auth/auth";
import { API_CONFIG } from "../../config/api.config";



export const signUpApi = async (data: SignUpRequest): Promise<string> => {
  const response = await fetch(`${API_CONFIG.BASE_URL}/auth/signUp`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  const text = await response.text(); 

  if (!response.ok) {
    throw new Error(text); 
  }

  return text; 
};