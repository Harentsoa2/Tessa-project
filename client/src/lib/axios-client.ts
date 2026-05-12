import { CustomError } from "@/types/custom-error.type";
import axios from "axios";

const baseURL = import.meta.env.VITE_API_BASE_URL;

const options = {
  baseURL,
  withCredentials: true,
  timeout: 10000,
};

const API = axios.create(options);

API.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const response = axios.isAxiosError(error) ? error.response : undefined;
    const data = response?.data;
    const status = response?.status;
    const requestUrl = axios.isAxiosError(error) ? error.config?.url : "";
    const message = getErrorMessage(data, error);

    if (
      status === 401 &&
      !requestUrl?.includes("/user/current") &&
      window.location.pathname !== "/sign-in"
    ) {
      window.location.href = "/sign-in";
    }

    const customError = Object.assign(
      error instanceof Error ? error : new Error(message),
      {
        message,
        errorCode: getErrorCode(data),
      }
    ) as CustomError;

    return Promise.reject(customError);
  }
);

type ErrorPayload = {
  error?: string;
  errorCode?: string;
  message?: string;
};

const isErrorPayload = (data: unknown): data is ErrorPayload =>
  typeof data === "object" && data !== null;

const getErrorMessage = (data: unknown, error: unknown) => {
  if (typeof data === "string") return data;
  if (isErrorPayload(data)) {
    return data.message || data.error || "Request failed";
  }
  if (error instanceof Error) return error.message;
  return "Request failed";
};

const getErrorCode = (data: unknown) => {
  if (isErrorPayload(data)) return data.errorCode || "UNKNOWN_ERROR";
  return "UNKNOWN_ERROR";
};

export default API;
