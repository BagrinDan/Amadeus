export interface SignUpRequest{
    username: string;
    password: string;
    confirmPassword: string;
}

export interface SignUpResponse{
    message: string;
}