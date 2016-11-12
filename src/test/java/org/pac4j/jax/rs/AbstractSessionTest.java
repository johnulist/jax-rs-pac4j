package org.pac4j.jax.rs;

import static org.assertj.core.api.Assertions.assertThat;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.junit.Test;

/**
 *
 * @author Victor Noel - Linagora
 * @since 1.0.0
 *
 */
public abstract class AbstractSessionTest extends AbstractTest {

    @Override
    protected Class<?> getResource() {
        return TestSessionResource.class;
    }

    protected abstract String cookieName();

    @Test
    public void testNotLogged() {
        final Response res = getTarget("/logged").request().get();
        assertThat(res.getStatus()).isEqualTo(401);
    }

    @Test
    public void testLogin() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final Response login = getTarget("/login").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(login.getStatus()).isEqualTo(302);

        final NewCookie cookie = login.getCookies().get(cookieName());
        assertThat(cookie).isNotNull();

        final String ok = getTarget("/logged").request().cookie(cookie).get(String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void testInject() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "foo");
        final Response login = getTarget("/login").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        assertThat(login.getStatus()).isEqualTo(302);

        final NewCookie cookie = login.getCookies().get(cookieName());
        assertThat(cookie).isNotNull();

        final String ok = getTarget("/inject").request().cookie(cookie).get(String.class);
        assertThat(ok).isEqualTo("ok");
    }

    @Test
    public void testLoginFail() {
        Form form = new Form();
        form.param("username", "foo");
        form.param("password", "bar");
        final Response res = getTarget("/login").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        // TODO this should be a 401, see https://github.com/pac4j/pac4j/issues/704
        assertThat(res.getStatus()).isEqualTo(403);

    }
}
