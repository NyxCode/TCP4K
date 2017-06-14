import de.nyxcode.tcp4k.Connection
import de.nyxcode.tcp4k.ListenerHandler
import de.nyxcode.tcp4k.register
import org.junit.Test

class Benchmark {

    val handler = ListenerHandler.create()

    @Test
    fun benchmark() {
        registerListener(handler)
        val n = 1000
        val res = doBenchmark(handler, n)
        println(res)
    }

    fun registerListener(handler: ListenerHandler) {
        val n = 40
        repeat(n) { handler.register<A> { _, _ -> } }
        repeat(n) { handler.register<B> { _, _ -> } }
        repeat(n) { handler.register<C> { _, _ -> } }
        repeat(n) { handler.register<D> { _, _ -> } }
        repeat(n) { handler.register<E> { _, _ -> } }
        repeat(n) { handler.register<F> { _, _ -> } }
        repeat(n) { handler.register<G> { _, _ -> } }
        repeat(n) { handler.register<A1> { _, _ -> } }
        repeat(n) { handler.register<B1> { _, _ -> } }
        repeat(n) { handler.register<C1> { _, _ -> } }
        repeat(n) { handler.register<D1> { _, _ -> } }
        repeat(n) { handler.register<E1> { _, _ -> } }
        repeat(n) { handler.register<F1> { _, _ -> } }
        repeat(n) { handler.register<G1> { _, _ -> } }
        repeat(n) { handler.register<Any> { _, _ -> } }
    }

    fun doBenchmark(handler: ListenerHandler, n: Int): Long {
        val instances = arrayOf(A(), B(), C(), D(), E(), F(), G(), A1(), B1(), C1(), D1(), E1(), F1(), G1())
        val pseudoCon = object : Connection {
            override fun get(key: String) = TODO("not implemented")
            override fun set(key: String, value: Any?) = TODO("not implemented")
            override val channel get() = TODO("not implemented")
        }
        val started = System.currentTimeMillis()
        repeat(n) {
            instances.forEach {
                handler.trigger(pseudoCon, it)
            }
        }
        return System.currentTimeMillis() - started
    }

    open class A
    open class B
    open class C
    open class D
    open class E
    open class F
    open class G
    class A1 : A()
    class B1 : B()
    class C1 : C()
    class D1 : D()
    class E1 : E()
    class F1 : F()
    class G1 : G()
}