package com.project.shared.data.funcs;

import com.google.common.base.Function;



/**
 * This abstract implementation of {@link com.google.common.base.Function} is a more streamlined way for chaining
 * functions than guava's {@link com.google.common.base.Functions} library
 */
public abstract class Func<A, B> implements Function<A,B>
{
    public abstract B apply(A arg);

    public <C> Func<A, C> then(final Function<B, C> next) {
        final Func<A, B> that = this;
        return new Func<A, C> () {
            @Override
            public C apply(A arg)
            {
                return next.apply(that.apply(arg));
            }
        };
    }

    public <C> AsyncFunc<A,C> then(final AsyncFunc<B, C> next)
    {
        return AsyncFunc.fromFunc(this).then(next);
    }

    /**
     * Converts this Func to one with any result type. The result value will always be the value given 'res'.
     */
    public <C> Func<A,C> constResult(C res)
    {
        return this.then(Func.<B,C>constFunc(res));
    }

    public <C> Func<C,B> constArg(final A constArg)
    {
        return Func.<C,A>constFunc(constArg).then(this);
    }

    public static <A,B> Func<A, B> constFunc(final B value)
    {
        return new Func<A,B>(){
            @Override public B apply(A arg) {
                return value;
            }};
    }

    public static <A,B> Func<A,B> fromFunction(final Function<A,B> func)
    {
        return new Func<A,B>() {
            @Override public B apply(A arg) {
                return func.apply(arg);
            }};
    }

    public static abstract class Action<A> extends Func<A, Void>
    {
        public abstract void exec(A arg);
        @Override
        public Void apply(A arg)
        {
            this.exec(arg);
            return null;
        }

        public static Action<Object> empty = new Action<Object>() {
            @Override public void exec(Object arg) {}
        };

        @SuppressWarnings("unchecked")
        public static <T> Action<T> empty() {
            return (Action<T>)empty;
        }
    }

    public static abstract class VoidAction extends Action<Void>
    {
        public abstract void exec();

        @Override public void exec(Void arg) {
            this.exec();
        }
    }

}
