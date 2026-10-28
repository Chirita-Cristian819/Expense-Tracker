export function readCsrfToken() {
    return document.cookie
        .split('; ')
        .find(cookie => cookie.startsWith('XSRF-TOKEN='))
        ?.split('=')[1];
}