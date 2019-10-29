package j2ll;

import org.objectweb.asm.Opcodes;

import java.util.*;

/**
 * Block of Local Vars (for 1 method)
 */
public final class LocalVarTable {

    //Map<Integer, LocalVar> vars = new HashMap<Integer, LocalVar>();

    List<LocalVar> all = new ArrayList<>(); // a slot maybe 2 or more var in deference bytecode range

    List<String> labelsForParse = new ArrayList<>();
    List<String> labelsForUse = new ArrayList<>();
    int maxSlot = 0;

    public void addVar(LocalVar localVar) {
        all.add(localVar);
        if (maxSlot < localVar.slot) {
            maxSlot = localVar.slot;
            if (localVar.signature.equals("D") || localVar.signature.equals("J")) {
                maxSlot += 1;
            }
        }
        //vars.put(localVar.slot, localVar);
    }

    public int getMaxSlot() {
        return maxSlot + 1;
    }

    public void addParseLabel(String lab) {
        labelsForParse.add(lab);
        //System.out.println("label count " + labelsForParse.size());
    }

    public void addUseLabel(String lab) {
        labelsForUse.add(lab);
        //System.out.println("label count " + labelsForParse.size());
    }

    public LocalVar get(int slot, String lab) {

        List<LocalVar> list = getBySlot(slot);
        Collections.sort(list, new Comparator<LocalVar>() {
            @Override
            public int compare(LocalVar o1, LocalVar o2) {
                if (o1.endAt == o2.startAt) {
                    throw new RuntimeException("local var sort error,expect not overwrite.");
                }
                return o1.endAt - o2.startAt;
            }
        });

        int labIndex = labelsForUse.indexOf(lab);
        if (labIndex < 0) {
            throw new RuntimeException("can't found label: " + lab);
        }
        for (LocalVar lv : list) {
            if (labIndex < lv.endAt) {
                return lv;
            }
        }
        throw new RuntimeException("can't found localvar by slot: " + slot);
    }

    public List<LocalVar> getAll() {
        return all;
    }

    public List<LocalVar> getBySlot(int i) {
        List<LocalVar> list = new ArrayList<>();
        for (LocalVar lv : all) {
            if (lv.slot == i) {
                list.add(lv);
            }
        }
        return list;
    }

    /**
     * same slot would be more than 1 vars ,every var  have a rang in lab1 to lab2
     * so a lab vist , check this lab range var
     */
    public void parseRange(String name, String lab1, String lab2, int slot) {
        for (LocalVar lv : all) {
            if (lv.name.equals(name) && lv.slot == slot) {
                lv.startAt = labelsForParse.indexOf(lab1);
                lv.endAt = labelsForParse.indexOf(lab2);
                lv.name = lv.name + "_" + lv.startAt + "_" + lv.endAt;
            }
        }
    }

//    public void activeByFrame(int type, int num, Object[] para) {
//        if(true)return;
//        //
//        switch (type) {
//            case Opcodes.F_APPEND: { //1
//                int varsize=vars.size();
//                for (int i = 0; i < num; i++) {
//                    int slot = varsize + i;
//                    for (LocalVar lv : getBySlot(slot)) {
//                        if (lv.signature.equals(para[slot])) {
//                            vars.put(lv.slot, lv);
//                        }
//                    }
//                }
//                break;
//            }
//            case Opcodes.F_CHOP: {//2
//                for (int i = 0; i < num; i++) {
//                    int slot = para.length - i;
//                    for (LocalVar lv : getBySlot(slot)) {
//                        if (lv.signature.equals(para[slot])) {
//                            vars.put(lv.slot, lv);
//                        }
//                    }
//                }
//                break;
//            }
//            case Opcodes.F_FULL: {//0
//                break;
//            }
//            case Opcodes.F_NEW: {//-1
//                break;
//            }
//            case Opcodes.F_SAME: {//3
//                break;
//            }
//            case Opcodes.F_SAME1: {//4
//                break;
//            }
//            default: {
//                break;
//            }
//        }
//
//    }
//
//    public void activeVars(String lab) {
//        labelsForUse.add(lab);
//        int idx = labelsForUse.indexOf(lab);
//        for (LocalVar lv : all) {
//            if (lv.startAt == idx) {   //enter range of effect
//                vars.put(lv.slot, lv);
//                //System.out.println("active:"+lv.name);
//            }
//            if (lv.endAt == idx) {      //lever range of effect
//                //vars.remove(lv.slot);
//                //System.out.println("close:"+lv.name);
//
//                //oracle jdk compiled class , using a loclavar is out of the localvariabletable in method attribute
//                for (LocalVar tmp : all) {
//                    if (tmp.slot == lv.slot && tmp.endAt != lv.endAt) {
//                        vars.put(tmp.slot, tmp);
//                    }
//                }
//            }
//        }
//    }

}
