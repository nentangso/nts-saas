package org.nentangso.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Internal call with client credentials, client-registration-id=internal
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@FeignClient
public @interface NtsAuthorizedFeignClient {
    @AliasFor(annotation = FeignClient.class, attribute = "name")
    String name() default "";

    /**
     * A custom {@code @Configuration} for the feign client.
     *
     * Can contain override {@code @Bean} definition for the pieces that
     * make up the client, for instance {@link feign.codec.Decoder},
     * {@link feign.codec.Encoder}, {@link feign.Contract}.
     *
     * @return the custom {@code @Configuration} for the feign client.
     * @see FeignClientsConfiguration for the defaults.
     */
    @AliasFor(annotation = FeignClient.class, attribute = "configuration")
    Class<?>[] configuration() default NtsAuthorizedFeignConfiguration.class;

    /**
     * An absolute URL or resolvable hostname (the protocol is optional).
     * @return the URL.
     */
    String url() default "";

    /**
     * Whether 404s should be decoded instead of throwing FeignExceptions.
     * @return true if 404s will be decoded; false otherwise.
     */
    boolean decode404() default false;

    /**
     * Fallback class for the specified Feign client interface. The fallback class must
     * implement the interface annotated by this annotation and be a valid Spring bean.
     *
     * @return the fallback class for the specified Feign client interface.
     */
    Class<?> fallback() default void.class;

    /**
     * Path prefix to be used by all method-level mappings.
     *
     * @return the path prefix to be used by all method-level mappings.
     */
    String path() default "";
}
