package com.cjwsjy.app.dial;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cjwsjy.app.dial.view.ContactView;
import com.do1.cjmobileoa.db.model.SUserVO;
import com.cjwsjy.app.R;

@SuppressLint("ResourceAsColor")
public class ContactAdapter extends BaseAdapter
// implements Filterable
{

	private List<SUserVO> contacts = new ArrayList<SUserVO>();
	private Context mContext;
	private int display_Mode = ContactView.Display_Mode_Display;

	public ContactAdapter(Context context, int display) {
		mContext = context;
		display_Mode = display;
	}

	public void clear() {
		// preQueryString = "";
		contacts.clear();
	}

	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder = null;
		if (convertView == null) 
		{
			holder = new ViewHolder();
			ContactView contactView = new ContactView(mContext, display_Mode);
			holder.contactView = contactView;
			contactView.setTag(holder);
		}
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}
		try 
		{
			SUserVO svo = contacts.get(position);
			holder.contactView.setContact(contacts.get(position));
			holder.contactView.build();
		} 
		catch (Exception e) 
		{

		}
	      if (position == selectItem) {  
	    	  holder.contactView.nameTextView.setSelected(true);
          }  else{
        	  holder.contactView.nameTextView.setSelected(false);
          }

		return holder.contactView;
	}

	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	private int selectItem = -1;

	public SUserVO getSelectedItem() {
		if (selectItem != -1) {
			return contacts.get(selectItem);
		}
		return null;
	}

	class ViewHolder {
		ContactView contactView;
	}

	// public void sortContact(ArrayList<SUserVO> lis) {
	// ContactComparator comparator = new ContactComparator();
	// Collections.sort(lis, comparator);
	// }
	//
	// public class ContactComparator implements Comparator<SUserVO> {
	//
	// @Override
	// public int compare(SUserVO lhs, SUserVO rhs) {
	// if (lhs.matchValue.score > rhs.matchValue.score) {
	// return -1;
	// } else if (lhs.matchValue.score == rhs.matchValue.score) {
	// if (lhs.matchValue.matchType == Contact.Match_Type_Name
	// && lhs.matchValue.nameIndex < lhs.fullNamesString
	// .size()
	// && rhs.matchValue.nameIndex < rhs.fullNamesString
	// .size()) {
	// return lhs.fullNamesString.get(lhs.matchValue.nameIndex)
	// .compareTo(
	// rhs.fullNamesString
	// .get(rhs.matchValue.nameIndex));
	// }
	// return 0;
	// } else {
	// return 1;
	// }
	// }
	// }

	public List<SUserVO> getContacts() {
		return contacts;
	}

	public void setContacts(List<SUserVO> contacts) 
	{
		this.contacts = contacts;
		notifyDataSetChanged();
	}

	// @Override
	// public Filter getFilter() {
	// return filter;
	// }
	//
	// private String preQueryString = "";
	//
	// private Filter filter = new Filter() {
	// @SuppressWarnings("unchecked")
	// @Override
	// protected void publishResults(CharSequence constraint,
	// FilterResults results) {
	// if (results != null) {
	// setContacts((ArrayList<EmployeeVO>) results.values);
	// if (results.count > 0) {
	// notifyDataSetChanged();
	// } else {
	// notifyDataSetInvalidated();
	// }
	// }
	// }
	//
	// @Override
	// synchronized protected FilterResults performFiltering(CharSequence
	// constraint) {
	// // 手速过快的话，有可能在执行这里的时候正在执行getView，这时候却修改了contact的内容，有可能报错
	// // 所以这时候有三种方法解决这个问题。
	// // 一是同步getView和performFiltering方法，让他们不相互打断，这很难实现，得重新实现adapter,listView
	// // 二是执行performFiltering不修改contacts列表，这就要求使用contacts列表的一个clone，但是这样效率低下
	// // 三是仍然允许performFiltering方法修改contacts内容，但是要在getView方法里做好预案
	// // 当发现数据已经变得有问题的时候，直接返回不做处理，而当performFiltering执行完毕后再执行publishResults后。
	// // 联系人列表将迅速发生改变，这样肉眼无法识别其实有那么20毫秒的时候里有几个联系人的匹配内容显示有问题。
	// // 第三种方法要求performFiltering使用synchronized，并且setContacts(resultList)要写在此方法中
	//
	// // 2014-09-29 11:01:11 update
	// //
	// 上一次修改只处理了修改了单个contact的问题，但是还有另一个问题：setContacts();之后并没有立即notifyDataSetChanged();
	// //
	// 在notifyDataSetChanged之后，adapter会顺序执行getView，但是在getView的时候，setContacts可能又会执行，
	// // 从而改变了contacts的长度,contacts.get(position)可能会发生越界的问题，因此这时候getView要捕获这个错误
	// // 返回一个空view，跟上次一样，空view存在时间很短，不会有人注意的……
	// if (TextUtils.isEmpty(constraint)
	// || (preQueryString != null && preQueryString.equals(constraint))) {
	// return null;
	// }
	//
	// String queryString = constraint.toString();
	// FilterResults results = new FilterResults();
	// ArrayList<EmployeeVO> baseList = new ArrayList<EmployeeVO>();
	// ArrayList<EmployeeVO> resultList = new ArrayList<EmployeeVO>();
	// // 点击过快的话，第一个publishResults还没执行到，第二个performFiltering就已经开始了，
	// // 如果使用contacts做baseList的话有可能导致搜索不到。
	// // 就算是使用AllContacts做baseList基本没有问题，Nexus5 270联系人搜索不超过10ms
	//
	// baseList = AppApplication.AllContacts;
	// for (Iterator<EmployeeVO> iterator = baseList.iterator(); iterator
	// .hasNext();) {
	// EmployeeVO contact = (EmployeeVO) iterator.next();
	// if (contact.match(queryString) > 0) {
	// resultList.add(contact);
	// }
	// }
	// sortContact(resultList);
	// preQueryString = queryString;
	// results.values = resultList;
	// results.count = resultList.size();
	// return results;
	// }
	// };
}
