package com.project.shared.data.funcs;

import com.project.shared.data.Pair;
import com.project.shared.data.funcs.Func.Action;
import com.project.shared.utils.ThrowableUtils;
import com.project.shared.utils.loggers.Logger;


public abstract class AsyncFunc<A, B>  {
    protected abstract <S, E> void run(A arg, Func<B, S> successHandler, Func<Throwable, E> errorHandler);

    public void run(A arg) {
        //Logger.log("Starting AsyncFunc with arg: " + arg);
        this.run(arg, Action.<B>empty(), Action.<Throwable>empty());
    }

    public <C> AsyncFunc<A, C> then(final AsyncFunc<B, C> success)
    {
        return this.then(success, null);
    }

    public <C> AsyncFunc<A, C> then(Func<B, C> success)
    {
        return this.then(success, null);
    }

    public <C> AsyncFunc<A, C> then(Func<B, C> success, Func<Throwable, C> error)
    {
        return this.then(null == success ? null : AsyncFunc.fromFunc(success), null == error ? null : AsyncFunc.fromFunc(error));
    }

    public <C> AsyncFunc<A, C> then(final AsyncFunc<B, C> success, final AsyncFunc<Throwable, C> recover)
    {
        return this.thenSelect(AsyncFunc.<B,AsyncFunc<B,C>>constFunc(success), recover);
    }

    public <C> AsyncFunc<A, C> thenSelect(final AsyncFunc<B, AsyncFunc<B, C>> successSelector, final AsyncFunc<Throwable, C> recover)
    {
        return AsyncFunc.<A,B,C>chain(this, successSelector, recover);
    }

    public <C> AsyncFunc<A, C> thenSelect(final AsyncFunc<B, AsyncFunc<B,C>> successSelector)
    {
        return this.thenSelect(successSelector, null);
    }

    public <C> AsyncFunc<A, C> thenSelect(final Func<B, AsyncFunc<B,C>> successSelector)
    {
        return this.thenSelect(AsyncFunc.fromFunc(successSelector), null);
    }
    
    public AsyncFunc<A, Pair<B,B>> and(final AsyncFunc<A,B> other)
    {
        return AsyncFunc.<A,B>both(this, other);
    }

    /**
     * Converts this AsyncFunc to one with any result type. The result value will always be the value given 'res'.
     */
    public <C> AsyncFunc<A,C> constResult(C res)
    {
        return this.then(AsyncFunc.<B,C>constFunc(res));
    }

    public <C> AsyncFunc<C,B> constArg(final A constArg)
    {
        return AsyncFunc.<C,A>constFunc(constArg).then(this);
    }

    public static <A,B> AsyncFunc<A, B> constFunc(final B value)
    {
        return new AsyncFunc<A,B>(){
            @Override
            protected <S, E> void run(A arg, final Func<B, S> successHandler, Func<Throwable, E> errorHandler)
            {
                successHandler.apply(value);
            }};
    }

    private static <A,B,C> AsyncFunc<A, C> chain(final AsyncFunc<A, B> first, final AsyncFunc<B, AsyncFunc<B, C>> secondSelector, final AsyncFunc<Throwable, C> recover)
    {
        return new AsyncFunc<A,C>(){
            @Override
            protected <S, E> void run(A arg, final Func<C, S> successHandler, final Func<Throwable, E> errorHandler)
            {
                //Logger.log("Starting chained ('then') AsyncFunc with arg: " + arg);
                first.run(arg, new Action<B>() {
                    @Override
                    public void exec(final B arg)
                    {
                        //Logger.log("Chained AsyncFunc succeeded, next in chain with arg: " + arg);
                        secondSelector.run(arg, new Func<AsyncFunc<B,C>,S>(){
                            @Override public S apply(final AsyncFunc<B, C> success) {
                                //Logger.log("Chained AsyncFunc succeeded, next in chain with arg: " + arg);
                                if (null != success) {
                                    success.run(arg, successHandler, errorHandler);
                                }
                                return null;
                            }}, new Action<Throwable> () {
                            @Override public void exec(Throwable arg) {
                                //Logger.log("Chained AsyncFunc failed, next in chain with arg: " + arg);
                                if (null != recover) {
                                    recover.run(arg, successHandler, errorHandler);
                                }
                            }});
                    }
                }, new Action<Throwable>() {
                    @Override public void exec(Throwable arg)
                    {
                        //Logger.log("Chained AsyncFunc failed, next in chain with arg: " + arg);
                        if (null != recover) {
                            recover.run(arg, successHandler, errorHandler);
                        }
                    }
                });
            }
        };
    }

    private static <A,B> AsyncFunc<A, Pair<B,B>> both(final AsyncFunc<A, B> left, final AsyncFunc<A, B> right)
    {
        return new AsyncFunc<A, Pair<B,B>>() {
            @Override
            protected <S, E> void run(A arg, final Func<Pair<B, B>, S> successHandler, Func<Throwable, E> errorHandler)
            {
                final int[] numCompleted = {0};
                Func<B, S> singleSuccessHandler = new Func<B, S>(){
                    private B _firstResult;

                    @Override
                    public S apply(B arg)
                    {
                        numCompleted[0] += 1;

                        if (1 == numCompleted[0]) {
                            this._firstResult = arg;
                            return null;
                        }
                        // Both completed
                        return successHandler.apply(new Pair<B, B>(this._firstResult, arg));
                    }};

                left.run(arg, singleSuccessHandler, Action.<Throwable>empty());
                right.run(arg, singleSuccessHandler, Action.<Throwable>empty());
            }
        };
    }

    public static <A,B> AsyncFunc<A, B> fromFunc(final Func<A,B> func)
    {
        return new AsyncFunc<A,B>() {
            @Override
            protected <S,E> void run(A arg, Func<B, S> successHandler, Func<Throwable, E> errorHandler)
            {
                B res = null;
                try {
                    res = func.apply(arg);
                }
                catch (Throwable e) {
                	// TODO perhaps use the errorHandler here?
                	Logger.info(this, "Error in AsyncFunc: " + e.toString());
                	Logger.info(this, ThrowableUtils.joinStackTrace(e));
            		throw new RuntimeException(e);
                }
                successHandler.apply(res);
            }
        };
    }


    public static <A> AsyncFunc<A, A> immediate()
    {
        return new AsyncFunc<A, A>() {
            @Override
            protected <S, E> void run(A arg, Func<A, S> successHandler, Func<Throwable, E> errorHandler)
            {
                successHandler.apply(arg);
            }

            @Override
            public <C> AsyncFunc<A, C> then(final AsyncFunc<A, C> success, final AsyncFunc<Throwable, C> recover) {
                return success;
            }
        };
    }
};
