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

package org.gradle.model.internal.core;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import org.gradle.api.Action;
import org.gradle.api.Nullable;
import org.gradle.api.internal.ClosureBackedAction;
import org.gradle.model.ModelViewClosedException;
import org.gradle.model.RuleSource;
import org.gradle.model.collection.CollectionBuilder;
import org.gradle.model.internal.core.rule.describe.ModelRuleDescriptor;
import org.gradle.model.internal.type.ModelType;

import java.util.Iterator;
import java.util.Set;

import static org.gradle.internal.Cast.uncheckedCast;

// TODO - mix in Groovy support and share with managed set
public class SpecializedCollectionBuilderGroovyDecorator<I> extends GroovyObjectSupport implements ClosableCollectionBuilder<I> {
    private final CollectionBuilder<I> rawInstance;
    private ModelType<? extends CollectionBuilder<I>> type;
    private ModelRuleDescriptor ruleDescriptor;
    private boolean closed;

    public SpecializedCollectionBuilderGroovyDecorator(CollectionBuilder<I> rawInstance, ModelType<? extends CollectionBuilder<I>> type, ModelRuleDescriptor ruleDescriptor) {
        this.rawInstance = rawInstance;
        this.type = type;
        this.ruleDescriptor = ruleDescriptor;
    }

    @Override
    public String toString() {
        return rawInstance.toString();
    }

    @Override
    public <S> CollectionBuilder<S> withType(Class<S> withType) {
        CollectionBuilder<S> ses = rawInstance.withType(withType);
        return new SpecializedCollectionBuilderGroovyDecorator<S>(ses, DefaultCollectionBuilder.typeOf(withType), ruleDescriptor);
    }

    @Override
    public int size() {
        return rawInstance.size();
    }

    @Override
    public boolean isEmpty() {
        return rawInstance.isEmpty();
    }

    @Nullable
    @Override
    public I get(String name) {
        return rawInstance.get(name);
    }

    @Nullable
    @Override
    public I get(Object name) {
        return rawInstance.get(name);
    }

    @Override
    public boolean containsKey(Object name) {
        return rawInstance.containsKey(name);
    }

    @Override
    public boolean containsValue(Object item) {
        return rawInstance.containsValue(item);
    }

    @Override
    public Set<String> keySet() {
        return rawInstance.keySet();
    }

    @Override
    public Iterator<I> iterator() {
        return rawInstance.iterator();
    }

    @Override
    public void create(String name) {
        assertNotClosed();
        rawInstance.create(name);
    }

    @Override
    public void create(String name, Action<? super I> configAction) {
        assertNotClosed();
        rawInstance.create(name, configAction);
    }

    @Override
    public <S extends I> void create(String name, Class<S> type) {
        assertNotClosed();
        rawInstance.create(name, type);
    }

    @Override
    public <S extends I> void create(String name, Class<S> type, Action<? super S> configAction) {
        assertNotClosed();
        rawInstance.create(name, type, configAction);
    }

    @Override
    public void named(String name, Action<? super I> configAction) {
        assertNotClosed();
        rawInstance.named(name, configAction);
    }

    @Override
    public void named(String name, Class<? extends RuleSource> ruleSource) {
        assertNotClosed();
        rawInstance.named(name, ruleSource);
    }

    @Override
    public void beforeEach(Action<? super I> configAction) {
        assertNotClosed();
        rawInstance.beforeEach(configAction);
    }

    @Override
    public <S> void beforeEach(Class<S> type, Action<? super S> configAction) {
        assertNotClosed();
        rawInstance.beforeEach(type, configAction);
    }

    @Override
    public void all(Action<? super I> configAction) {
        assertNotClosed();
        rawInstance.all(configAction);
    }

    @Override
    public <S> void withType(Class<S> type, Action<? super S> configAction) {
        assertNotClosed();
        rawInstance.withType(type, configAction);
    }

    @Override
    public <S> void withType(Class<S> type, Class<? extends RuleSource> rules) {
        rawInstance.withType(type, rules);
    }

    @Override
    public void afterEach(Action<? super I> configAction) {
        assertNotClosed();
        rawInstance.afterEach(configAction);
    }

    @Override
    public <S> void afterEach(Class<S> type, Action<? super S> configAction) {
        assertNotClosed();
        rawInstance.afterEach(type, configAction);
    }

    // TODO - mix this in and validate closure parameters
    public void create(String name, Closure<? super I> configAction) {
        create(name, new ClosureBackedAction<I>(configAction));
    }

    // TODO - mix this in and validate closure parameters
    public <S extends I> void create(String name, Class<S> type, Closure<? super S> configAction) {
        create(name, type, new ClosureBackedAction<I>(configAction));
    }

    // TODO - mix this in and validate closure parameters
    public void named(String name, Closure<? super I> configAction) {
        named(name, new ClosureBackedAction<I>(configAction));
    }

    // TODO - mix this in and validate closure parameters
    public void all(Closure<? super I> configAction) {
        all(new ClosureBackedAction<I>(configAction));
    }

    // TODO - mix this in and validate closure parameters
    public <S> void withType(Class<S> type, Closure<? super S> configAction) {
        withType(type, new ClosureBackedAction<S>(configAction));
    }

    // TODO - mix this in and validate closure parameters
    public void beforeEach(Closure<? super I> configAction) {
        beforeEach(new ClosureBackedAction<I>(configAction));
    }

    // TODO - mix this in and validate closure parameters
    public <S> void beforeEach(Class<S> type, Closure<? super S> configAction) {
        beforeEach(type, new ClosureBackedAction<S>(configAction));
    }

    // TODO - mix this in and validate closure parameters
    public void afterEach(Closure<? super I> configAction) {
        afterEach(new ClosureBackedAction<I>(configAction));
    }

    // TODO - mix this in and validate closure parameters
    public <S> void afterEach(Class<S> type, Closure<? super S> configAction) {
        afterEach(type, new ClosureBackedAction<S>(configAction));
    }

    // TODO - mix this in
    @Override
    public Object getProperty(String property) {
        I element = rawInstance.get(property);
        if (element == null) {
            throw new MissingPropertyException(property, CollectionBuilder.class);
        }
        return element;
    }

    // TODO - mix this in and validate closure parameters
    public Void methodMissing(String name, Object argsObj) {
        Object[] args = (Object[]) argsObj;
        if (args.length == 1 && args[0] instanceof Class<?>) {
            Class<? extends I> itemType = uncheckedCast(args[0]);
            create(name, itemType);
        } else if (args.length == 2 && args[0] instanceof Class<?> && args[1] instanceof Closure<?>) {
            Class<? extends I> itemType = uncheckedCast(args[0]);
            Closure<? super I> closure = uncheckedCast(args[1]);
            create(name, itemType, closure);
        } else if (args.length == 1 && args[0] instanceof Closure<?>) {
            Closure<? super I> closure = uncheckedCast(args[0]);
            named(name, closure);
        } else {
            throw new MissingMethodException(name, CollectionBuilder.class, args);
        }
        return null;
    }

    private void assertNotClosed() {
        if (closed) {
            throw new ModelViewClosedException(type, ruleDescriptor);
        }
    }

    @Override
    public void close() {
        this.closed = true;
    }
}

