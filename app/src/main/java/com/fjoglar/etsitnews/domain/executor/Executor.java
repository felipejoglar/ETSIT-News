/*
 * Copyright (C) 2016 Felipe Joglar Santos
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
package com.fjoglar.etsitnews.domain.executor;

import com.fjoglar.etsitnews.domain.interactor.base.UseCase;

/**
 * This executor is responsible for running interactors on background threads.
 */
public interface Executor {
    /**
     * This method should call the interactor's run method and thus start the
     * interactor. This should be called on a background thread as interactors
     * might do lengthy operations.
     *
     * @param interactor The interactor to run.
     */
    void execute(final UseCase interactor);
}
