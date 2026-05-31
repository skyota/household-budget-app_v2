package com.kakeibo.application.usecase.auth;

import org.springframework.stereotype.Service;

import com.kakeibo.application.exception.InvalidSessionException;
import com.kakeibo.domain.model.user.User;
import com.kakeibo.domain.repository.UserRepository;

@Service
public class GetCurrentUserUseCaseImpl implements GetCurrentUserUseCase {
    private final UserRepository userRepository;

    public GetCurrentUserUseCaseImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public GetCurrentUserResult get(GetCurrentUserQuery query) {
        User user = userRepository.findById(query.userId())
            .orElseThrow(() -> new InvalidSessionException("ユーザーが見つかりません。"));
        return new GetCurrentUserResult(user.getId(), user.getUsername());
    }
}
