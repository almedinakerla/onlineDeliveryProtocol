package onlineDeliveryProtocol;

import FSM.IFSM;
import FSM.FSM;
import MessageTemplate.Message;
import FSM.TcpTransportClient;
import FSM.Dispatcher;
import FSM.IMessage;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Admin extends FSM implements IFSM {
    static String TOKEN = "123";
    static int IDLE 					= 0;
    static int LOGIN 					= 1;
    static int WAITING_FOR_RESPONSE 	= 2;
    static int REGISTRATION 			= 3;
    static int LOGIN_AFTER_REGISTRATION = 4;
    static int LOGINED_ADMIN 			= 5;
    static int REVIEW_ITEM_LISTS_ADMIN 	= 6;
    static int VIEWING_LIST_ADMIN 		= 7;
    static int READY_TO_CONNECT			= 8;
    static int CONNECTING				= 9;


    public Admin(int id) {
        super(id);
    }
    @Override
    public void init() {
        setState(IDLE);

        // REGISTRACIJA I LOGIN

        addTransition(IDLE, new Message(Message.Types.RESOLVE_DOMAIN_NAME), "resolveDomain");
        addTransition(READY_TO_CONNECT, new Message(Message.Types.REGISTER_TO_SERVER), "registerOnServer");
        addTransition(CONNECTING, new Message(Message.Types.CONNECTED_SUCCESSFULL), "connectionSuccessful");
        addTransition(LOGIN, new Message(Message.Types.LOGIN_REQUEST), "loginRequest");
        addTransition(WAITING_FOR_RESPONSE, new Message(Message.Types.LOGIN_SUCCESSFUL_ADMIN), "loginSuccessAdmin");
        addTransition(WAITING_FOR_RESPONSE, new Message(Message.Types.REGISTRATION_REQUIRED), "registrationRequired");
        addTransition(REGISTRATION, new Message(Message.Types.REGISTRATION_REQUEST), "registrationRequest");
        addTransition(LOGIN_AFTER_REGISTRATION, new Message(Message.Types.LOGIN_AFTER_REG), "loginAfterRequest");

        // ITEMS
        addTransition(LOGINED_ADMIN, new Message(Message.Types.ASKING_FOR_ITEMS), "askingForItemsADMIN");
        addTransition(REVIEW_ITEM_LISTS_ADMIN,new Message(Message.Types.SENDING_ITEMS), "listingItemsADMIN");

        //ADMIN PRIVILEGES
        addTransition(VIEWING_LIST_ADMIN,new Message(Message.Types.ADD_ITEM), "addItem");
        addTransition(VIEWING_LIST_ADMIN,new Message(Message.Types.DELETE_ITEM), "deleteItem");
        addTransition(VIEWING_LIST_ADMIN,new Message(Message.Types.EDIT_ITEM), "deleteItem");

    }


    //
    // //
    // //   REGISTRACIJA I LOGIN
    // //
    //


    public void resolveDomain(IMessage message){
        Message msg = (Message)message;
        System.out.println("Resolving: " + msg.getParam(Message.Params.DOMAIN));
        //resolve domain, implement this please!
        Message tcpMSG = new Message(5555);
        tcpMSG.addParam(Message.Params.IP, "127.0.0.1");
        sendMessage(tcpMSG);
        setState(READY_TO_CONNECT);

    }
    public void registerOnServer(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.REGISTER_TO_SERVER);
        System.out.println("Connecting...");
        sendMessage(msg);
        setState(CONNECTING);
    }


    public void connectionSuccessful(IMessage message){
        System.out.println("Connected successful!");
        setState(LOGIN);
    }
    public void loginRequest(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.LOGIN_REQUEST);
        sendMessage(msg);
        System.out.println("Login...");
        System.out.println(msg.getParam("username") + msg.getParam("email") + msg.getParam("password") + msg.getParam("role"));
        setState(WAITING_FOR_RESPONSE);
    }
    public void loginSuccessAdmin(IMessage message){
        System.out.println("Admin login!");
        setState(LOGINED_ADMIN);
    }

    public void registrationRequired(IMessage message){
        System.out.println("Registration required");
        setState(REGISTRATION);
    }
    public void registrationRequest(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.REGISTRATION_REQUEST);
        sendMessage(msg);
        System.out.println("Registration...");
        System.out.println(msg.getParam("username") + msg.getParam("email") + msg.getParam("password") + msg.getParam("role"));
        setState(LOGIN_AFTER_REGISTRATION);
    }
    public void loginAfterRequest(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.LOGIN_AFTER_REG);
        sendMessage(msg);
        System.out.println("Registred.");
        System.out.println("Login after registration...");
        System.out.println(msg.getParam("username") + msg.getParam("email") + msg.getParam("password") + msg.getParam("role"));
        setState(WAITING_FOR_RESPONSE);
    }


    //
    // //
    // // ITEMS
    // //
    //

    public void askingForItemsADMIN(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.ASKING_FOR_ITEMS);
        sendMessage(msg);
        System.out.println("Sending request for items...");
        setState(REVIEW_ITEM_LISTS_ADMIN);
    }


    public void listingItemsADMIN(IMessage message){
        Message msg = (Message) message;
        ArrayList<Items> items = new ArrayList<Items>();
        items = (ArrayList<Items>)msg.getParam(Message.Params.ITEMS, true);
        System.out.println("Item list:");
        items.forEach(items1 -> System.out.println(items1));
        setState(VIEWING_LIST_ADMIN);
    }



    public void addItem(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.ADD_ITEM);
        System.out.println("Unos parametara");
        Scanner input = new Scanner(System.in);
        String item_name = input.nextLine();
        Integer item_count = input.nextInt();
        Items adding_item = new Items(item_name,item_count);
        msg.addParam(Message.Params.ITEMS, adding_item);
        sendMessage(msg);
        setState(REVIEW_ITEM_LISTS_ADMIN);
    }


    public void deleteItem(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.DELETE_ITEM);
        System.out.println("Unos parametara za brisanje");
        Scanner input = new Scanner(System.in);
        String item_name = input.nextLine();
        Integer item_count = input.nextInt();
        Items adding_item = new Items(item_name,item_count);
        msg.addParam(Message.Params.ITEMS, adding_item);
        sendMessage(msg);
        setState(REVIEW_ITEM_LISTS_ADMIN);
    }
    public void editItem(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.EDIT_ITEM);
        System.out.println("Promjena koli�?ine dostupnih proizvoda: ");
        Scanner input = new Scanner(System.in);
        String item_name = input.nextLine();
        Integer item_count = input.nextInt();
        Items adding_item = new Items(item_name,item_count);
        msg.addParam(Message.Params.ITEMS, adding_item);
        sendMessage(msg);
        setState(REVIEW_ITEM_LISTS_ADMIN);
    }


    static int SERVER_PORT = 9999;
    static String SERVER_URL = "";
    static String SERVER_IP = "";
    public static void main(String[] args) throws Exception{

        Client oDPclient = new Client(0);
        TcpTransportClient tcpFSM = new TcpTransportClient(5);
        tcpFSM.setServerPort(SERVER_PORT);
        tcpFSM.setReceiver(oDPclient);

        Dispatcher dis = new Dispatcher(false);
        dis.addFSM(oDPclient);
        dis.addFSM(tcpFSM);
        dis.start();


        Scanner input = new Scanner(System.in);

        String username = "";
        System.out.println("Enter URI of server in format username:port@host.ba");

        SERVER_PORT = 9999;
        SERVER_URL = "www.olx.ba";
        System.out.println("User: " + username);
        System.out.println("Host: " + SERVER_URL + ":" + SERVER_PORT);
        tcpFSM.setServerPort(SERVER_PORT);


        Message tempMsg = new Message(Message.Types.RESOLVE_DOMAIN_NAME);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.DOMAIN, SERVER_URL);
        dis.addMessage(tempMsg);

        tempMsg = new Message(Message.Types.REGISTER_TO_SERVER);
        tempMsg.setToId(0);
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.LOGIN_REQUEST);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.USERNAME, "benjo");
        tempMsg.addParam(Message.Params.PASSWORD, "123");
        tempMsg.addParam(Message.Params.EMAIL, "benjo@a.a");
        tempMsg.addParam(Message.Params.ROLE, "admin");
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.REGISTRATION_REQUEST);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.USERNAME, "benjo");
        tempMsg.addParam(Message.Params.PASSWORD, "123");
        tempMsg.addParam(Message.Params.EMAIL, "benjo@a.a");
        tempMsg.addParam(Message.Params.ROLE, "admin");
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.LOGIN_AFTER_REG);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.USERNAME, "benjo");
        tempMsg.addParam(Message.Params.PASSWORD, "123");
        tempMsg.addParam(Message.Params.EMAIL, "benjo@a.a");
        tempMsg.addParam(Message.Params.ROLE, "admin");
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.ASKING_FOR_ITEMS);
        tempMsg.setToId(0);
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.ADD_ITEM);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.USERNAME, "benjo");
        tempMsg.addParam(Message.Params.PASSWORD, "123");
        tempMsg.addParam(Message.Params.EMAIL, "benjo@a.a");
        tempMsg.addParam(Message.Params.ROLE, "admin");
        Items item = new Items("Laptop", 10);
        tempMsg.addParam(Message.Params.ITEMS, item);
        dis.addMessage(tempMsg);

        
        Thread.sleep(10000);

        tempMsg = new Message(Message.Types.DELETE_ITEM);
        tempMsg.setToId(0);
        Items new_item = new Items("Laptop", 10);
        tempMsg.addParam(Message.Params.ITEMS, new_item);
        dis.addMessage(tempMsg);
        

        while(true){


        }

    }
}
