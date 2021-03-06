package net.launcher.game.nbt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ci010
 */
public class NBTList extends NBT implements Iterable<NBT>
{
	private NBTType type = NBTType.NULL;
	private List<NBT> list = new ArrayList<>();

	NBTList(List<NBT> list)
	{
		this();
		this.list = list;
	}

	NBTList()
	{
		super(NBTType.LIST);
	}

	public int size()
	{
		return list.size();
	}

	public boolean add(NBT base)
	{
		if (base.getType() == NBTType.NULL) return false;
		if (validate(base)) if (list.add(base)) return true;
		return false;
	}

	public boolean addAll(List<? extends NBT> nbts)
	{
		for (NBT nbt : nbts)
			if (!validate(nbt)) return false;
		this.list.addAll(nbts);
		return true;
	}

	public NBT get(int i)
	{
		return list.get(i);
	}

	public NBT remove(int i)
	{
		return list.remove(i);
	}

	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	public NBT set(int i, NBT base)
	{
		if (base.getType() == NBTType.NULL) return null;
		if (validate(base))
			return list.set(i, base);
		return null;
	}

	private boolean validate(NBT base)
	{
		if (this.type == NBTType.NULL) type = base.getType();
		return this.type == base.getType();
	}

	public NBTType getContentType()
	{
		return type;
	}

	@Override
	public Iterator<NBT> iterator()
	{
		return list.iterator();
	}

	@Override
	public NBTList asList()
	{
		return this;
	}

	@Override
	public NBT clone()
	{
		NBTList copy = new NBTList();
		for (NBT nbt : this)
			copy.add(nbt.clone());
		return copy;
	}

	public Object[] toArray()
	{
		return list.toArray();
	}

	@Override
	public Object asRaw()
	{
		return this.list.stream().map(NBT::asRaw).collect(Collectors.toList());
	}

	public Stream<NBT> stream()
	{
		return list.stream();
	}

	@Override
	public boolean isList()
	{
		return true;
	}

	@Override
	public String toString()
	{
		return "NBTList{" +
				"type=" + type +
				", listStr=" + list +
				'}';
	}
}
