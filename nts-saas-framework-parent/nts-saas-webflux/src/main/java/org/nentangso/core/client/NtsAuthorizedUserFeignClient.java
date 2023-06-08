/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nentangso.core.client;

import org.springframework.core.annotation.AliasFor;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactivefeign.spring.config.ReactiveFeignClient;

import java.lang.annotation.*;

/**
 * Annotation for interfaces declaring that a REST client with that interface should be
 * created (e.g. for autowiring into another component).
 * <p>
 * patterned after org.springframework.cloud.netflix.feign.FeignClient
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ReactiveFeignClient
public @interface NtsAuthorizedUserFeignClient {

    /**
     * The name of the service with optional protocol prefix. Synonym for {@link #name()
     * name}. A name must be specified for all clients, whether or not a url is provided.
     * Can be specified as property key, eg: ${propertyKey}.
     *
     * @return the client name
     */
    @AliasFor(annotation = ReactiveFeignClient.class, attribute = "name")
    String value() default "";

    /**
     * The service id with optional protocol prefix. Synonym for {@link #value() value}.
     *
     * @return the client name
     */
    @AliasFor(annotation = ReactiveFeignClient.class, attribute = "value")
    String name() default "";

    /**
     * Sets the <code>@Qualifier</code> value for the feign client.
     *
     * @return the qualifier name
     */
    String qualifier() default "";

    /**
     * An absolute URL or resolvable hostname (the protocol is optional).
     *
     * @return the URL.
     */
    String url() default "";

    /**
     * Whether 404s should be decoded instead of throwing FeignExceptions
     *
     * @return true if 404s will be decoded; false otherwise.
     */
    boolean decode404() default false;

    /**
     * A custom <code>@Configuration</code> for the feign client. Can contain override
     * <code>@Bean</code> definition for the pieces that make up the client, for instance
     * {@link ReactiveHttpRequestInterceptor}, {@link feign.Contract}.
     *
     * @return the custom {@code @Configuration} for the feign client.
     * @see NtsAuthorizedUserFeignConfiguration for the defaults
     */
    Class<?>[] configuration() default NtsAuthorizedUserFeignConfiguration.class;

    /**
     * Fallback class for the specified Feign client interface. The fallback class must
     * implement the interface annotated by this annotation and be a valid spring bean.
     *
     * @return the fallback class for the specified Feign client interface.
     */
    Class<?> fallback() default void.class;

    /**
     * Define a fallback factory for the specified Feign client interface. The fallback
     * factory must produce instances of fallback classes that implement the interface
     * annotated by {@link NtsAuthorizedUserFeignClient}. The fallback factory must be a valid spring
     * bean.
     *
     * @return the class
     */
    Class<?> fallbackFactory() default void.class;

    /**
     * Path prefix to be used by all method-level mappings. Can be used with or without
     * <code>@RibbonClient</code>.
     *
     * @return the path prefix to be used by all method-level mappings.
     */
    String path() default "";

    /**
     * Whether to mark the feign proxy as a primary bean. Defaults to true.
     *
     * @return the boolean
     */
    boolean primary() default true;

}
