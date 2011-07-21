package com.project.shared.data.funcs;

import com.project.shared.data.funcs.Func.Action;


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
        return chain(this, success, recover);
    }

    private <C> AsyncFunc<A, C> chain(final AsyncFunc<A, B> first, final AsyncFunc<B, C> success, final AsyncFunc<Throwable, C> recover)
    {
        return new AsyncFunc<A,C>(){
            @Override
            protected <S, E> void run(A arg, final Func<C, S> successHandler, final Func<Throwable, E> errorHandler)
            {
                //Logger.log("Starting chained ('then') AsyncFunc with arg: " + arg);
                first.run(arg, new Action<B>() {
                    @Override
                    public void exec(B arg)
                    {
                        //Logger.log("Chained AsyncFunc succeeded, next in chain with arg: " + arg);
                        if (null != success) {
                            success.run(arg, successHandler, errorHandler);
                        }
                    }
                }, new Action<Throwable>() {

                    @Override
                    public void exec(Throwable arg)
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


    public static <A,B> AsyncFunc<A, B> fromFunc(final Func<A,B> func)
    {
        return new AsyncFunc<A,B>() {
            @Override
            protected <S,E> void run(A arg, Func<B, S> successHandler, Func<Throwable, E> errorHandler)
            {
                B res = null;
                try {
                    res = func.call(arg);
                }
                catch (Throwable e) {
                    errorHandler.call(e);
                    return;
                }
                successHandler.call(res);
            }
        };
    }


    public static <A> AsyncFunc<A, A> immediate()
    {
        return new AsyncFunc<A, A>() {
            @Override
            protected <S, E> void run(A arg, Func<A, S> successHandler, Func<Throwable, E> errorHandler)
            {
                successHandler.call(arg);
            }

            @Override
            public <C> AsyncFunc<A, C> then(final AsyncFunc<A, C> success, final AsyncFunc<Throwable, C> recover) {
                return success;
            }
        };
    }
};
