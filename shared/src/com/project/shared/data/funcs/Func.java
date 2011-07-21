package com.project.shared.data.funcs;



public abstract class Func<A, B>
{
    public abstract B call(A arg);

    public <C> Func<A, C> then(final Func<B, C> next) {
        final Func<A, B> that = this;
        return new Func<A, C> () {
            @Override
            public C call(A arg)
            {
                return next.call(that.call(arg));
            }
        };
    }

    public static abstract class Action<A> extends Func<A, Void>
    {
        public abstract void exec(A arg);
        @Override
        public Void call(A arg)
        {
            this.exec(arg);
            return null;
        }

        public static Action<Object> empty = new Action<Object>() {
            @Override
            public void exec(Object arg) {}
        };

        @SuppressWarnings("unchecked")
        public static <T> Action<T> empty() {
            return (Action<T>)empty;
        }
    }

    public static abstract class VoidAction extends Action<Void>
    {
        public abstract void exec();

        @Override
        public void exec(Void arg)
        {
            this.exec();
        }
    }
}
