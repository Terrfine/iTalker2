package net.rong.italker.factory.data.group;

import android.text.TextUtils;

import net.rong.italker.factory.data.helper.DbHelper;
import net.rong.italker.factory.data.helper.GroupHelper;
import net.rong.italker.factory.data.helper.UserHelper;
import net.rong.italker.factory.data.user.UserCenter;
import net.rong.italker.factory.data.user.UserDispatcher;
import net.rong.italker.factory.model.card.GroupCard;
import net.rong.italker.factory.model.card.GroupMemberCard;
import net.rong.italker.factory.model.db.Group;
import net.rong.italker.factory.model.db.GroupMember;
import net.rong.italker.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GroupDispatcher implements GroupCenter{

    private static GroupCenter instance;
    //单线程池；处理卡片一个个的消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static GroupCenter instance(){
        if(instance == null){
            //避免重复创建
            synchronized (GroupDispatcher.class){
                if(instance == null)
                    instance = new GroupDispatcher();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(GroupCard... cards) {
        if(cards == null || cards.length == 0)
            return;
        //丢到单线程池中
        executor.execute(new GroupHandler(cards));
    }

    @Override
    public void dispatch(GroupMemberCard... cards) {
        if(cards == null || cards.length == 0)
            return;
        //丢到单线程池中
        executor.execute(new GroupMemberHandler(cards));
    }

    private class GroupMemberHandler implements Runnable {

        private final GroupMemberCard[] cards;

        private GroupMemberHandler(GroupMemberCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<GroupMember> members = new ArrayList<>();
            for(GroupMemberCard card : cards){
                if(card == null || TextUtils.isEmpty(card.getGroupId() )|| TextUtils.isEmpty(card.getUserId()))
                    continue;
                //成员对应的人的信息
                User user = UserHelper.search(card.getUserId());
                //成员对应的群的信息
                Group group = GroupHelper.find(card.getGroupId());
                if(user != null | group != null){
                    GroupMember member = card.build(group, user);
                    members.add(member);
                }
            }
            if(members.size() > 0)
                DbHelper.save(GroupMember.class, members.toArray(new GroupMember[0]));
        }
    }

    /**
     * 把群Card处理为群DB类
     */
    private class GroupHandler implements Runnable{
        private final GroupCard[] cards;

        private GroupHandler(GroupCard[] cards) {
            this.cards = cards;
        }


        @Override
        public void run() {
            List<Group> groups = new ArrayList<>();
            for(GroupCard card : cards){
                if(card == null || TextUtils.isEmpty(card.getOwnerId()))
                    continue;
                User owner = UserHelper.search(card.getOwnerId());
                if(owner != null){
                    Group group = card.build(owner);
                    groups.add(group);
                }
            }
            if(groups.size() > 0){
                DbHelper.save(Group.class, groups.toArray(new Group[0]));
            }
        }
    }
}
