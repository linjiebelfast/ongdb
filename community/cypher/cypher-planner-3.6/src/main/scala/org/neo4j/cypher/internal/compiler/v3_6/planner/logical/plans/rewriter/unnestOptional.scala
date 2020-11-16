/*
 * Copyright (c) 2018-2020 "Graph Foundation"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * ONgDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.compiler.v3_6.planner.logical.plans.rewriter

import org.neo4j.cypher.internal.v3_6.logical.plans._
import org.neo4j.cypher.internal.v3_6.expressions.{Ands, Expression}
import org.neo4j.cypher.internal.v3_6.util.attribution.{IdGen, SameId}
import org.neo4j.cypher.internal.v3_6.util.{Rewriter, bottomUp}

case object unnestOptional extends Rewriter {

  override def apply(input: AnyRef) = if (isSafe(input)) instance.apply(input) else input

  import org.neo4j.cypher.internal.v3_6.util.Foldable._

  /*
   * It is not safe to unnest an optional expand with when we have
   * a merge relationship, since it must be able to read its own
   * writes
   */
  private def isSafe(input: AnyRef) = !input.treeExists {
        case _:MergeCreateRelationship => true
  }

  private val instance: Rewriter = bottomUp(Rewriter.lift {

    case apply:AntiConditionalApply => apply

    case apply@Apply(lhs,
      Optional(
      e@Expand(_: Argument, _, _, _, _, _, _), _)) =>
        optionalExpand(e, lhs)(Seq.empty)(SameId(apply.id))

    case apply@Apply(lhs,
      Optional(
      Selection(Ands(predicates),
      e@Expand(_: Argument, _, _, _, _, _, _)), _)) =>
        optionalExpand(e, lhs)(predicates.toSeq)(SameId(apply.id))
  })

  private def optionalExpand(e: Expand, lhs: LogicalPlan): Seq[Expression] => IdGen => OptionalExpand =
    predicates => idGen => OptionalExpand(lhs, e.from, e.dir, e.types, e.to, e.relName, e.mode, predicates)(idGen)
}
