package japgolly.scalajs

import japgolly.scalajs.react.raw.ReactComponent

import scalajs.js
import org.scalajs.dom

/*
Bad approaches
==============
* Building Types via conjunction - too hard to map
* JS + implicit ops - extern JS types can't be changed
* PSBN = annoying. PS usually enough.


[ ] Prevent certain lifecycle methods being called in certain scopes.
[ ] Make easy to add functionality (such as Id/CallbackTo, S zoom, P map).
[ ] All components: Id/Callback.
[ ] All components: S zoom.
[ ] All components: P map.
[ ] Typify PropsChildren.
[ ] Easily facade JS components.
[ ] Easily facade JS ES6 components.
[ ] Create ES6 components in Scala.
*/

package object react {

  type Callback = CallbackTo[Unit]

  /*
  case class CtorLike[In, P, Out](apply: (In, P) => Out) extends AnyVal

  @inline implicit class CtorLikeOps[In, P, Out](ctor: In)(implicit like: CtorLike[In, P, Out]) {
    @inline def apply(p: P): Out =
      like.apply(ctor, p)
    @inline def cmap[P2](f: P2 => P) =
      new MappedCtor[In, P, P2, Out](ctor, f)(like)
  }

  case class ReactComponentU[P <: js.Object](r: raw.ReactComponent[P])

  class MappedCtor[In, P0 <: js.Object, P, Out](val underlying: In, val f: P => P0)(implicit val like: CtorLike[In, P0, Out])
  implicit def likeMappedCtor[In, P0 <: js.Object, P, Out] = CtorLike[MappedCtor[In, P0 , P, Out], P, Out](
    (c, p) => c.like.apply(c.underlying, c.f(p)))

  class JsClassCtor[P <: js.Object](val cls: raw.ReactClass[P])
  implicit def likeJsClassCtor[P <: js.Object] = CtorLike[JsClassCtor[P], P, ReactComponentU[P]](
    (c, p) => ReactComponentU(c.cls(p)))

  class ScalaClassCtor[P](val js: JsClassCtor[Box[P]]) {
//    def backend
  }
  implicit def likeScalaClassCtor[P] = CtorLike[ScalaClassCtor[P], P, ReactComponentU[Box[P]]](
    (c, p) => c.js(Box(p)))

  // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  abstract class TEST {
    type P <: js.Object
    def p: P

    def jsClassCtor: JsClassCtor[P]
    jsClassCtor(p)

    jsClassCtor.cmap(???).apply(???)

  }
  */

//  class JsClassCtor[P <: js.Object](val raw: Raw.ReactClass[P]) extends AnyVal {
//    def apply(props: P): CompU[P] =
//      new CompU(raw(props))
//  }
//
//  class CompU[P <: js.Object](val raw: Raw.ReactComponent[P]) extends AnyVal {
//
//    def render(container: Raw.ReactDOM.Container): CompM[P] = {
//      val m: Raw.ReactComponent[_] = Raw.ReactDOM.render(raw.render(), container)
//      new CompM(m.asInstanceOf[Raw.ReactComponent[P]])
//    }
//  }
//
//  class CompM[P <: js.Object](val raw: Raw.ReactComponent[P]) extends AnyVal {
//
//  }

  import scalajs.js.|
  def orNullToOption[A](an: A | Null): Option[A] =
    Option(an.asInstanceOf[A])

  type Key = String | Boolean | raw.JsNumber

  // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
/*
  implicit def toPlainMountedNessNess[P <: js.Object, S <: js.Object]: raw.ReactComponent => CompJs3X.Mounted[P, S] =
    r => new CompJs3X.Mounted[P, S] {
      override val rawInstance = r
    }

  object CompJs3 {
    type Constructor[P <: js.Object, S <: js.Object] = CompJs3X.Constructor[P, S, CompJs3X.Mounted[P, S]]
    type Constructor_NoProps[S <: js.Object] = CompJs3X.Constructor_NoProps[S, CompJs3X.Mounted[Null, S]]

    def Constructor[P <: js.Object, S <: js.Object](r: raw.ReactClass): Constructor[P, S]      = CompJs3X.Constructor(r)
    def Constructor_NoProps[S <: js.Object]        (r: raw.ReactClass): Constructor_NoProps[S] = CompJs3X.Constructor_NoProps(r)
  }

  object CompJs3X {

    trait HasRaw {
      val rawInstance: raw.ReactComponent

      final def rawDyn: js.Dynamic =
        rawInstance.asInstanceOf[js.Dynamic]
    }

    case class Constructor[P <: js.Object, S <: js.Object, M](rawCls: raw.ReactClass) {
      def apply(props: P): Unmounted[P, S, M] =
        new Unmounted(raw.React.createElement(rawCls, props))
    }

    case class Constructor_NoProps[S <: js.Object, M](rawCls: raw.ReactClass) {
      private val instance: Unmounted[Null, S, M] =
        new Constructor(rawCls).apply(null)

      def apply(): Unmounted[Null, S, M] =
        instance
    }

    class Unmounted[P <: js.Object, S <: js.Object, M](val rawElement: raw.ReactComponentElement) {

      def key: Option[Key] =
        orNullToOption(rawElement.key)

      def ref: Option[String] =
        orNullToOption(rawElement.ref)

      def props: P =
        rawElement.props.asInstanceOf[P]

      def children: raw.ReactNodeList =
        rawElement.props.children

      def renderIntoDOM(container: raw.ReactDOM.Container, callback: js.ThisFunction = null)
                       (implicit b: raw.ReactComponent => M): M =
        b(raw.ReactDOM.render(rawElement, container, callback))
    }

    trait Mounted[P <: js.Object, S <: js.Object] extends HasRaw {
      //      def getDefaultProps: Props
      //      def getInitialState: js.Object | Null
      //      def render(): ReactElement

      final def isMounted(): Boolean =
        rawInstance.isMounted()

      final def props: P =
        rawInstance.props.asInstanceOf[P]

      final def children: raw.ReactNodeList =
        rawInstance.props.children

      final def state: S =
        rawInstance.state.asInstanceOf[S]

      final def setState(state: S, callback: Callback = Callback.empty): Unit =
        rawInstance.setState(state, callback.toJsFn)

      final def getDOMNode(): dom.Element =
        raw.ReactDOM.findDOMNode(rawInstance)
    }
  }
  */

  // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

  object CompJs3 {
    type Constructor[P <: js.Object, S <: js.Object] = CompJs3X.Constructor[P, S, CompJs3X.Mounted[P, S, raw.ReactComponent]]
    type Constructor_NoProps[S <: js.Object] = CompJs3X.Constructor_NoProps[S, CompJs3X.Mounted[Null, S, raw.ReactComponent]]

    def Constructor[P <: js.Object, S <: js.Object](r: raw.ReactClass): Constructor[P, S] =
      CompJs3X.Constructor(r)(CompJs3X.Mounted[P, S, raw.ReactComponent])

    def Constructor_NoProps[S <: js.Object](r: raw.ReactClass): Constructor_NoProps[S] =
      CompJs3X.Constructor_NoProps(r)(CompJs3X.Mounted[Null, S, raw.ReactComponent])
  }

  object CompJs3X {

    case class Constructor[P <: js.Object, S <: js.Object, M](rawCls: raw.ReactClass)(m: raw.ReactComponent => M) {
      def mapMounted[MM](f: M => MM): Constructor[P, S, MM] =
        new Constructor(rawCls)(f compose m)

      def apply(props: P): Unmounted[P, S, M] =
        new Unmounted(raw.React.createElement(rawCls, props), m)
    }

    case class Constructor_NoProps[S <: js.Object, M](rawCls: raw.ReactClass)(m: raw.ReactComponent => M) {
      def mapMounted[MM](f: M => MM): Constructor_NoProps[S, MM] =
        new Constructor_NoProps(rawCls)(f compose m)

      private val instance: Unmounted[Null, S, M] =
        new Constructor(rawCls)(m)(null)

      def apply(): Unmounted[Null, S, M] =
        instance
    }

    class Unmounted[P <: js.Object, S <: js.Object, M](val rawElement: raw.ReactComponentElement, m: raw.ReactComponent => M) {

      def key: Option[Key] =
        orNullToOption(rawElement.key)

      def ref: Option[String] =
        orNullToOption(rawElement.ref)

      def props: P =
        rawElement.props.asInstanceOf[P]

      def children: raw.ReactNodeList =
        rawElement.props.children

      def mapMounted[MM](f: M => MM): Unmounted[P, S, MM] =
        new Unmounted(rawElement, f compose m)

      def renderIntoDOM(container: raw.ReactDOM.Container, callback: js.ThisFunction = null): M =
        m(raw.ReactDOM.render(rawElement, container, callback))
    }

    def Mounted[P <: js.Object, S <: js.Object, Raw <: raw.ReactComponent](r: Raw): Mounted[P, S, Raw] =
      new Mounted[P, S, Raw] {
        override val rawInstance = r
      }

    trait Mounted[P <: js.Object, S <: js.Object, Raw <: raw.ReactComponent] {
      val rawInstance: Raw

      def addRawType[T <: js.Object]: Mounted[P, S, Raw with T] =
        this.asInstanceOf[Mounted[P, S, Raw with T]]

      //      def getDefaultProps: Props
      //      def getInitialState: js.Object | Null
      //      def render(): ReactElement

      final def isMounted(): Boolean =
        rawInstance.isMounted()

      final def props: P =
        rawInstance.props.asInstanceOf[P]

      final def children: raw.ReactNodeList =
        rawInstance.props.children

      final def state: S =
        rawInstance.state.asInstanceOf[S]

      final def setState(state: S, callback: Callback = Callback.empty): Unit =
        rawInstance.setState(state, callback.toJsFn)

      final def getDOMNode(): dom.Element =
        raw.ReactDOM.findDOMNode(rawInstance)
    }

  }

}
