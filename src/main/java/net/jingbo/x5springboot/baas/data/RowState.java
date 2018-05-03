package net.jingbo.x5springboot.baas.data;

import net.jingbo.x5springboot.baas.Utils;

public enum RowState {
	NONE, NEW, EDIT, DELETE;
	
	public static RowState parse(String state) {
		return  Utils.isEmptyString(state) ? RowState.NONE : RowState.valueOf(state.toUpperCase());
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

}
