package com.raquo.airstream.combine.generated

import com.raquo.airstream.combine.CombineSignalN
import com.raquo.airstream.signal.Signal

/** @param combinator Must not throw!*/
class CombineSignal3[T1, T2, T3, Out](
  protected[this] val parent1: Signal[T1],
  protected[this] val parent2: Signal[T2],
  protected[this] val parent3: Signal[T3],
  combinator: (T1, T2, T3) => Out
) extends CombineSignalN[Any, Out](
  parents = List(parent1, parent2, parent3),
  combinator = seq => combinator(
    seq(0).asInstanceOf[T1],
    seq(1).asInstanceOf[T2],
    seq(2).asInstanceOf[T3],
  )
)
