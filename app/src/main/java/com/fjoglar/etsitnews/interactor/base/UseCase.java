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
package com.fjoglar.etsitnews.interactor.base;

import com.fjoglar.etsitnews.executor.Executor;
import com.fjoglar.etsitnews.executor.MainThread;

/**
 * This abstract class implements some common methods for all interactors.
 * Cancelling an interactor, check if its running and finishing an interactor
 * has mostly the same code.
 *
 * Field methods are declared volatile as we might use these methods from different
 * threads (mainly from UI).
 *
 * For example, when an activity is getting destroyed then we should probably cancel
 * an interactor but the request will come from the UI thread unless the request was
 * assigned to a background thread.
 */
public abstract class UseCase implements Interactor {

    protected Executor mThreadExecutor;
    protected MainThread mMainThread;

    protected volatile boolean mIsCanceled;
    protected volatile boolean mIsRunning;

    public UseCase(Executor threadExecutor, MainThread mainThread) {
        mThreadExecutor = threadExecutor;
        mMainThread = mainThread;
    }

    /**
     * This method contains the actual business logic of the interactor.
     * It SHOULD NOT BE USED DIRECTLY but, instead, a developer should call the execute()
     * method of an interactor to make sure the operation is done on a background thread.
     *
     * This method should only be called directly while doing unit/integration tests.
     * That is the only reason it is declared public as to help with easier testing.
     */
    public abstract void run();

    public void cancel() {
        mIsCanceled = true;
        mIsRunning = false;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public void onFinished() {
        mIsRunning = false;
        mIsCanceled = false;
    }

    public void execute() {
        this.mIsRunning = true;
        mThreadExecutor.execute(this);
    }

}
