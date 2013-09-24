/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mongodb.connection

import org.mongodb.Fixture
import spock.lang.Specification

class UsageTrackingConnectionSpecification extends Specification {

    def 'generation is initialized'() {
        when:
        def connection = new UsageTrackingInternalConnection(new TestInternalConnectionFactory().create(Fixture.primary), 1);

        then:
        connection.generation == 1
    }

    def 'openAt should be set on open'() {
        when:
        def connection = new UsageTrackingInternalConnection(new TestInternalConnectionFactory().create(Fixture.primary), 0);

        then:
        connection.openedAt <= System.currentTimeMillis()
    }

    def 'lastUsedAt should be set on open'() {
        when:
        def connection = new UsageTrackingInternalConnection(new TestInternalConnectionFactory().create(Fixture.primary), 0);

        then:
        connection.lastUsedAt <= System.currentTimeMillis()
    }

    def 'lastUsedAt should be set on sendMessage'() {
        given:
        def connection = new UsageTrackingInternalConnection(new TestInternalConnectionFactory().create(Fixture.primary), 0);
        Thread.sleep(5);

        when:
        connection.sendMessage(Arrays.asList(), 1)

        then:
        connection.lastUsedAt <= System.currentTimeMillis()
    }

    def 'lastUsedAt should be set on receiveMessage'() {
        given:
        def connection = new UsageTrackingInternalConnection(new TestInternalConnectionFactory().create(Fixture.primary), 0);

        when:
        connection.receiveMessage()

        then:
        connection.lastUsedAt <= System.currentTimeMillis()
    }
}
