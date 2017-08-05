package com.NLPFramework.Files;

import com.NLPFramework.Crosscutting.Logger;

/**
 *
 * @author Héctor Llorens
 * @since 2011
 */
public class PlainFile extends NLPFile {

    public PlainFile(String filename) {
        super(filename);
    }
    
    private final static String format="PLAIN";

    @Override
    public String toPlain(String filename){
        throw new UnsupportedOperationException("Already a plain file");
    }

    @Override
    public Boolean isWellFormatted() {
        try {
            if (super.getFile()==null) {
                throw new Exception("No file loaded in NLPFile object");
            }
        } catch (Exception e) {
            Logger.WriteError("Errors found ("+this.getClass().getSimpleName()+"):\n\t" + e.toString() + "\n", e);
            return false;
        }
        return true;
    }



}
