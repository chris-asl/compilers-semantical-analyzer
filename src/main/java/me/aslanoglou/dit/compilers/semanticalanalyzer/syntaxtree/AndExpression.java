//
// Generated by JTB 1.3.2 DIT@UoA patched
//

package me.aslanoglou.dit.compilers.semanticalanalyzer.syntaxtree;

import me.aslanoglou.dit.compilers.semanticalanalyzer.visitor.GJNoArguVisitor;
import me.aslanoglou.dit.compilers.semanticalanalyzer.visitor.GJVisitor;
import me.aslanoglou.dit.compilers.semanticalanalyzer.visitor.GJVoidVisitor;
import me.aslanoglou.dit.compilers.semanticalanalyzer.visitor.Visitor;

/**
 * Grammar production:
 * f0 -> Clause()
 * f1 -> "&&"
 * f2 -> Clause()
 */
public class AndExpression implements Node {
   public Clause f0;
   public NodeToken f1;
   public Clause f2;

   public AndExpression(Clause n0, NodeToken n1, Clause n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public AndExpression(Clause n0, Clause n1) {
      f0 = n0;
      f1 = new NodeToken("&&");
      f2 = n1;
   }

   public void accept(Visitor v) {
      v.visit(this);
   }
   public <R,A> R accept(GJVisitor<R,A> v, A argu) {
      return v.visit(this,argu);
   }
   public <R> R accept(GJNoArguVisitor<R> v) {
      return v.visit(this);
   }
   public <A> void accept(GJVoidVisitor<A> v, A argu) {
      v.visit(this,argu);
   }
}
