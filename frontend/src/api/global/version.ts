import { API_CONFIG } from "../../config/api.config";


export const getVersion = async (): Promise<string> => {
    const res = await fetch(`${API_CONFIG.BASE_URL})/version`);
    return res.text();
}