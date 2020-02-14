package lc.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author liuchaoOvO on 2019/4/28
 */
public class MyAuthenticationException extends AuthenticationException
{

    public MyAuthenticationException(String msg)
    {
        super(msg);
    }
}
