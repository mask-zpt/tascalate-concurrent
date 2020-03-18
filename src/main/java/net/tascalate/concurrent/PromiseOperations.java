
/**
 * Copyright 2015-2020 Valery Silaev (http://vsilaev.com)
 *
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
package net.tascalate.concurrent;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class PromiseOperations {
    private PromiseOperations() {}

    // Lifted is somewhat questionable, but here it exists for symmetry with dropped()
    public static <T> Promise<Promise<T>> lift(CompletionStage<? extends T> promise) {
        return lift(Promises.from(promise));
    }
    
    public static <T> Promise<Promise<T>> lift(Promise<? extends T> promise) {
        return promise.dependent()
                      .thenApply(Promises::<T>success, true)
                      .unwrap();
    }
    
    public static <T> Promise<T> drop(CompletionStage<? extends CompletionStage<T>> promise) {
        return drop(Promises.from(promise));
    }
    
    public static <T> Promise<T> drop(Promise<? extends CompletionStage<T>> promise) {
        return promise.dependent()
                      .thenCompose(Promises::from, true)
                      .unwrap();
    }

    public static <T> Promise<Stream<T>> streamResult(CompletionStage<? extends T> promise) {
        return streamResult(Promises.from(promise));
    }

    public static <T> Promise<Stream<T>> streamResult(Promise<? extends T> promise) {
        return promise.dependent()
                      .handle((r, e) -> null == e ? Stream.<T>of(r) : Stream.<T>empty(), true)
                      .unwrap();
    }

    public static <T> Promise<Optional<T>> optionalResult(CompletionStage<? extends T> promise) {
        return optionalResult(Promises.from(promise));
    }
    
    public static <T> Promise<Optional<T>> optionalResult(Promise<? extends T> promise) {
        return promise.dependent()
                      .handle((r, e) -> Optional.<T>ofNullable(null == e ? r : null), true)
                      .unwrap();
    }
}
