package scalafix.util

import scala.meta._
import scalafix.internal.util.EagerInMemorySemanticdbIndex
import scalafix.v1.SemanticContext
import scalafix.v0._

/** An index for looking up data in a scala.meta.Database. */
trait SemanticdbIndex extends SemanticContext {

  /** Classpath built this SemanticdbIndex. */
  def classpath: Classpath

  /** The underlying raw database. */
  def database: Database

  /** Get all Input for this index. */
  def inputs: Seq[Input] = documents.map(_.input)

  /** Shorthand for scala.meta.Database.documents */
  def documents: Seq[Document] = database.documents

  /** Shorthand for scala.meta.Database.messages */
  def messages: Seq[Message] = database.messages

  /** Shorthand for scala.meta.Database.symbols */
  def symbols: Seq[ResolvedSymbol] = database.symbols

  /** Shorthand for scala.meta.Database.synthetics */
  def synthetics: Seq[Synthetic] = database.synthetics

  /** The resolved names in this database.
    *
    * Includes resolved name in synthetics, such as inferred implicits/types.
    */
  def names: Seq[ResolvedName]

  /** Lookup symbol at this position. */
  def symbol(position: Position): Option[Symbol]

  /** Lookup symbol at this tree.
    *
    * This method returns the same result as symbol(Tree.Position) in most cases
    * but handles some special cases:
    * - when tree is Term/Type.Select(_, name), query by name.position
    * - workaround for https://github.com/scalameta/scalameta/issues/1083
    */
  def symbol(tree: Tree): Option[Symbol]

  /** Lookup denotation of this symbol. */
  def denotation(symbol: Symbol): Option[Denotation]

  /** Lookup denotation of this tree.
    *
    * Shorthand method for symbol(tree).flatMap(denotation).
    */
  def denotation(tree: Tree): Option[Denotation]

  /** Build new SemanticdbIndex with only these documents. */
  def withDocuments(documents: Seq[Document]): SemanticdbIndex

  object Symbol {
    def unapply(tree: Tree): Option[Symbol] = symbol(tree)
    def unapply(pos: Position): Option[Symbol] = symbol(pos)
  }
}

object SemanticdbIndex {
  val empty: SemanticdbIndex =
    EagerInMemorySemanticdbIndex(Database(Nil), Classpath(Nil))

  @deprecated(
    "Use scalafix.internal.v0.LegacyInMemorySemanticdbIndex from scalafix-reflect instead",
    "0.6.0")
  def load(classpath: Classpath): SemanticdbIndex =
    throw new UnsupportedOperationException
}
