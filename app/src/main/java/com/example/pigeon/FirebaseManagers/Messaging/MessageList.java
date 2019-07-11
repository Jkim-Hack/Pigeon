package com.example.pigeon.FirebaseManagers.Messaging;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;


public class MessageList<E> extends LinkedList<E> {

    private ArrayList<MessageListListener> listeners = new ArrayList<>();

    public MessageList(){
        super();
    }
    public MessageList(Collection<? extends  E> collection) {
        super(collection);
    }

    @Override
    public boolean offer(E e){
        boolean res = super.offer(e);
        notifyAllListeners();
        return res;
    }

    public void addListener(MessageListListener listener){
        listeners.add(listener);
    }

    private void notifyAllListeners() {
        for (MessageListListener listener: listeners) {
            listener.OnMessageOffer();
        }
    }



}
