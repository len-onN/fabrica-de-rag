import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

// Intercepts mutating requests to append the CSRF Token from cookie
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  let modifiedReq = req;

  // For mutating methods, find XSRF-TOKEN in cookies and append as X-XSRF-TOKEN header
  if (['POST', 'PUT', 'PATCH', 'DELETE'].includes(req.method)) {
    const csrfToken = getCookie('XSRF-TOKEN');
    if (csrfToken) {
      modifiedReq = req.clone({
        headers: req.headers.set('X-XSRF-TOKEN', csrfToken)
      });
    }
  }

  // Ensure withCredentials is true to send session cookies
  if (req.url.startsWith('/api')) {
    modifiedReq = modifiedReq.clone({
      withCredentials: true
    });
  }

  return next(modifiedReq);
};

function getCookie(name: string): string | null {
  const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
  if (match) {
    return match[2];
  }
  return null;
}
