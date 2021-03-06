/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.platform.base.internal.rules

import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.internal.PolymorphicNamedEntityInstantiator
import org.gradle.model.internal.core.rule.describe.SimpleModelRuleDescriptor
import spock.lang.Specification

class DefaultRuleAwarePolymorphicNamedEntityInstantiatorTest extends Specification {

    def delegate = Mock(PolymorphicNamedEntityInstantiator)
    def instantiator = new DefaultRuleAwarePolymorphicNamedEntityInstantiator(delegate)

    def "uses delegate to create objects"() {
        given:
        delegate.create("foo", String) >> "bar"

        expect:
        instantiator.create("foo", String) == "bar"
    }

    def "uses delegate to register factories"() {
        given:
        def factory = Mock(NamedDomainObjectFactory)

        when:
        instantiator.registerFactory(String, factory, null)

        then:
        1 * delegate.registerFactory(String, factory)
    }

    def "throws error when a factory for the same type is registered more than once"() {
        given:
        instantiator.registerFactory(String, {}, new SimpleModelRuleDescriptor("test rule"))

        when:
        instantiator.registerFactory(String, {}, null)

        then:
        GradleException e = thrown()
        e.message == "Cannot register a factory for type String because a factory for this type was already registered by test rule."
    }
}
