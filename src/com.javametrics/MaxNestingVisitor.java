package com.javametrics;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

/**
 * Visitor of methods and constructors
 */
class MaxNestingVisitor extends GenericVisitorAdapter<Integer, Integer> {

    /**
     * Recursively count the max nesting of blocks of statements, no any other type of statement, only blocks add 1 up
     */
    public Integer deepRecursive(BlockStmt bs) {
        if (bs.getStmts().size() == 0) {
            return 0;
        } else {
            Integer max = 0;
            for( Node n : bs.getStmts() ) {

                if( n instanceof BlockStmt){
                    max = Math.max(max, deepRecursive((BlockStmt)n) );
                }else{
                    max = Math.max(max, deepRecursive(n) );
                }
            }
            return max+1;
        }
    }

    /**
     * Any other node can contain blocks of statements inside at any level
     */
    public Integer deepRecursive(Node n) {
        if (n.getChildrenNodes().size() == 0) {
            return 0;
        } else {
            Integer max = 0;
            for( Node ss : n.getChildrenNodes() ){

                if( ss instanceof BlockStmt){
                    max = Math.max(max, deepRecursive((BlockStmt)ss) );
                }else{
                    max = Math.max(max, deepRecursive(ss) );
                }
            }
            return max;
        }
    }

    @Override
    public Integer visit(ConstructorDeclaration n, Integer startingLine) {
        if( n.getBegin().line == startingLine ) {
            return deepRecursive(n.getBlock()) - 1;
        }else{
            return super.visit(n, startingLine);
        }
    }

    @Override
    public Integer visit(MethodDeclaration n, Integer startingLine) {
        if( n.getBegin().line == startingLine ) {
            if(n.getBody().getStmts().size() == 0){
                return 0;
            }
            return deepRecursive(n.getBody()) - 1;
        }else{
            return super.visit(n, startingLine);
        }
    }
}