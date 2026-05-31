package com.kakeibo.infrastructure.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kakeibo.application.exception.InvalidSessionException;
import com.kakeibo.application.usecase.auth.AuthenticationSessionResult;
import com.kakeibo.application.usecase.auth.AuthenticationSessionUseCase;
import com.kakeibo.domain.exception.DomainValidationException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationSessionUseCase authenticationSessionUseCase;

    public SessionAuthenticationFilter(AuthenticationSessionUseCase authenticationSessionUseCase) {
        this.authenticationSessionUseCase = authenticationSessionUseCase;
    }

    // 認証不要なパス（ログイン・登録・ヘルスチェック）
    private static final Set<String> PUBLIC_PATH = Set.of(
        "/user/register", "/user/login", "/api/health"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/quick/");
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 認証不要なパスはそのまま通す
        if (PUBLIC_PATH.contains(path)) { // pathがSet（PUBLIC_PATHS）の中に含まれているか
            filterChain.doFilter(request, response); // 認証チェックは不要なため次の処理へ進める
            return;
        }

        // CookieからセッションIDを取得
        String sessionValue = extractSessionCookie(request);

        if (sessionValue == null) {
            sendUnauthorized(response);
            return;
        }

        try {
            AuthenticationSessionResult result = authenticationSessionUseCase.authenticate(sessionValue);

            // ユーザーIDをリクエスト属性にセット → コントローラーで受け取れる
            request.setAttribute("authenticatedUserId", result.userId());
            filterChain.doFilter(request, response);
        } catch (InvalidSessionException | DomainValidationException e) {
            sendUnauthorized(response);
        }
    }

    private String extractSessionCookie(HttpServletRequest request) {
        // getCookie()はリクエストに入っているCookie全部を配列で返す
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies()) // Cookie[]をStreamに変換
            .filter(c -> "session".equals(c.getName())) // 名前が一致するものだけ残す
            .map(Cookie::getValue) // Cookieオブジェクト → 値（文字列）に変換（c -> c.getValue()と同じ意味）
            .findFirst() // 最初の1件をOptional<String>で取得
            .orElse(null); // 見つからなければnullを返す
    }

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        // HTTPステータスコードを401に設定（SC_UNAUTHORIZEDは401という数字に名前をつけたもの）
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // レスポンスのContent-Typeヘッダーを設定
        response.setContentType("application/json;charset=UTF-8");

        // レスポンスのボディにJSON文字列に書き込む
        // .getWriter()：HttpServletResponseから「文字を書き込む道具（Writer）」を取得する → ファイルに書き込むペンを取り出すイメージ
        // .write("書きたい文字")：Writerに文字列を書き込む → それがHTTPレスポンスのボディになる
        response.getWriter().write("""
            {
                "message":"認証が必要です。"
            }
            """);
    }
}
