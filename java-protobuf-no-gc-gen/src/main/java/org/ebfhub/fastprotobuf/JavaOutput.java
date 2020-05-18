package org.ebfhub.fastprotobuf;

import java.util.Stack;

class JavaOutput {
    enum IndentType{
        BRACE,
        CASE
    }
    private StringBuilder sb = new StringBuilder();
    private Stack<IndentType> indent = new Stack<>();

    private IndentType peek(){
        return indent.size()==0?null:indent.peek();
    }
    private void push(IndentType t){
        indent.push(t);
    }
    private void pop(IndentType t){
        if(indent.size()==0){
            sb.append("\n//pop expected "+t+" not empty\n");
            return;

        }
        IndentType k = indent.pop();
        if(k!=t){
            sb.append("\n//pop expected "+t+" not "+k+"\n");
        }
    }

    void line(String l) {

        if(l.indexOf('\n')!=-1){
            String[] parts = l.split("\n");
            for(String part:parts){
                line(part.trim());
            }
            return;
        }

        if (l.contains("}")&&!l.contains("{")) {
            pop(IndentType.BRACE);
        }
        for (int n = 0; n < indent.size(); n++) {
            sb.append("    ");
        }
        sb.append(l.trim());
        sb.append("\n");

        if (l.contains("{")&&!l.contains("}")) {
            push(IndentType.BRACE);
        }
        if ((l.contains("case ") || l.contains("default:")) ) {
            push(IndentType.CASE);
        }

        if (peek()== IndentType.CASE && ( l.contains("return")||l.contains("throw"))) {
            pop(IndentType.CASE);
        }
        if (l.contains("break;")) {
            pop(IndentType.CASE);
        }
    }

    void imports(Class<?>... cls){
        for(Class<?> cl:cls){
            line("import "+cl.getName()+";");
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return sb.toString();
    }

    /**
     * <p>blank.</p>
     */
    public void blank() {
        sb.append("\n");
    }
}
