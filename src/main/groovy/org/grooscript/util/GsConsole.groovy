/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.grooscript.util

@SuppressWarnings('Println')
class GsConsole {

    static final DESCRIPTION = '[grooscript]'

    private static getHead(origin) {
        origin?:DESCRIPTION
    }

    static error(message, origin = null) {
        println "\u001B[91m${getHead(origin)} - Error - ${message}\u001B[0m"
    }

    static message(message, origin = null) {
        println "${getHead(origin)} ${message}"
    }

    static info(message, origin = null) {
        println "${getHead(origin)} - Info - ${message}"
    }

    static exception(message, origin = null) {
        println "\u001B[91m${getHead(origin)} - Exception - ${message}\u001B[0m"
    }

    static debug(message, origin = null) {
        println "${getHead(origin)} - Debug - ${message}"
    }
}
