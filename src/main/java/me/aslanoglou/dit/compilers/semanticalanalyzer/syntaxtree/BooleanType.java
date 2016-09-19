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
 * f0 -> "boolean"
 */
public class BooleanType implements Node {
   public NodeToken f0;

   public BooleanType(NodeToken n0) {
      f0 = n0;
   }

   public BooleanType() {
      f0 = new NodeToken("boolean");
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

