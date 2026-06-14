import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { HomePage  } from "../pages/Home/Home";
import { SignInPage } from "../pages/Login/SignInPage";
import { SignUpPage } from "../pages/Register/SignUpPage"; 


const router = createBrowserRouter([
    {
        path: '/',
        element: <HomePage/ >
    },

    {
        path: '/login',
        element: <SignInPage/ >
    },
    
    {
        path: '/register',
        element: <SignUpPage/ >
    },

    {
        path: '*',
        element: <div style={{ padding: '20px' }}><h2>Not found 404</h2></div>
    }, 
]);

export const AppRouter = () => {
    return <RouterProvider router={router} />;
}