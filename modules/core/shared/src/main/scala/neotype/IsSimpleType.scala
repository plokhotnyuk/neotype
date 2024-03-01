package neotype

import scala.quoted.*

trait IsSimpleType[A]

object IsSimpleType:
  transparent inline given [A <: ValidatedWrapper[?]]: IsSimpleType[A] = ${
    isSimpleTypeImpl[A]
  }

  private def isSimpleTypeImpl[A: Type](using
      Quotes
  ): Expr[IsSimpleType[A]] =
    import quotes.reflect.*

    def getNt(t: TypeRepr): TypeRepr = t.widenTermRefByName match
      case Refinement(t, _, _) => t
      case AndType(t1, t2)     => getNt(t2)
      case t                   => t

    lazy val nt = getNt(TypeRepr.of[A])

    val hasDefinedValidateMethod = nt.typeSymbol.declaredMethods.exists(_.name == "validate")
    println(s"DECLARED METHODS: ${nt.typeSymbol.declaredMethods} FOR ${nt.show}")

    if hasDefinedValidateMethod then report.errorAndAbort(s"Newtype ${nt.show} is not a simple type")
    else
      '{
        new IsSimpleType[A] {}
      }

trait IsValidatedType[A]

object IsValidatedType:
  transparent inline given [A <: ValidatedWrapper[?]]: IsValidatedType[A] = ${
    isValidatedTypeImpl[A]
  }

  private def isValidatedTypeImpl[A: Type](using
      Quotes
  ): Expr[IsValidatedType[A]] =
    import quotes.reflect.*

    def getNt(t: TypeRepr): TypeRepr = t.widenTermRefByName match
      case Refinement(t, _, _) => t
      case AndType(t1, t2)     => getNt(t2)
      case t                   => t

    lazy val nt = getNt(TypeRepr.of[A])

    val hasDefinedValidateMethod = nt.typeSymbol.declaredMethods.exists(_.name == "validate")
    println(s"DECLARED METHODS IS VAL: ${nt.typeSymbol.declaredMethods} FOR ${nt.show}")
    if !hasDefinedValidateMethod then report.errorAndAbort(s"Newtype $nt is not a validated type")
    else
      '{
        new IsValidatedType[A] {}
      }
