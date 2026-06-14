import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { HomePage  } from "../pages/Home/Home";
import { LoginPage } from "../pages/Login/Login";

const router = createBrowserRouter([
    {
        path: '/',
        element: <HomePage/ >
    },

    {
        path: '/login',
        element: <LoginPage/ >
    },

    {
        path: '*',
        element: <div style={{ padding: '20px' }}><h2>Страница не найдена 404</h2></div>
    },
]);

export const AppRouter = () => {
    return <RouterProvider router={router} />;
}