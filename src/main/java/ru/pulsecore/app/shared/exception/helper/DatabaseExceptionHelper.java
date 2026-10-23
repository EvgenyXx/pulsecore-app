package ru.pulsecore.app.shared.exception.helper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import ru.pulsecore.app.player.infrastructure.exception.EmailAlreadyExistsException;
import ru.pulsecore.app.player.infrastructure.exception.PlayerNameAlreadyExistsException;
import ru.pulsecore.app.shared.exception.BaseException;

@UtilityClass
@Slf4j
public class DatabaseExceptionHelper {

    public BaseException translate(DataIntegrityViolationException ex) {
        if (ex.getCause() instanceof ConstraintViolationException cve) {
            DatabaseConstraint constraint = DatabaseConstraint.fromName(cve.getConstraintName());

            if (constraint != null) {
                return switch (constraint) {
                    case PLAYER_NAME, PLAYER_NAME_LEGACY -> new PlayerNameAlreadyExistsException();
                    case PLAYER_EMAIL -> new EmailAlreadyExistsException();
                    case OAUTH -> new BaseException(HttpStatus.BAD_REQUEST, "OAuth аккаунт уже привязан");
                };
            }
        }

        return new BaseException(HttpStatus.BAD_REQUEST, "Ошибка базы данных");
    }
}