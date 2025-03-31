package pl.radek.ss.dao;

public class QueryDB
{
	// -----------------------------------------------------------------------------------------------------
	public static String FIND_USERNAME 				= "SELECT a FROM Account a WHERE a.username = :username";
	public static String FIND_USERS 				= "SELECT a FROM Account AS a WHERE UPPER(a.username) LIKE #name";
	public static String GET_ACCOUNTS 				= "FROM Account a ORDER BY a.#orderBy #orderType";
	public static String FIND_EVENT 				= "SELECT e FROM Event AS e LEFT JOIN FETCH e.types AS t WHERE t.name IN (#eventType) AND UPPER(e.name) LIKE #name";
	public static String FIND_EVENT_WITH_USERNAME 	= "SELECT e FROM Event AS e LEFT JOIN FETCH e.types AS t WHERE t.name IN (#eventType) AND e.account.username LIKE #username AND UPPER(e.name) LIKE #name";
	public static String FIND_EVENT_BY_LINK 		= "SELECT e FROM Event AS e WHERE e.link LIKE #link";
	public static String GET_EVENTS 				= "FROM Event e LEFT JOIN FETCH e.types AS t WHERE t.name IN (#eventType) ORDER BY e.#orderBy #orderType";
	public static String GET_EVENTS_WITH_USERNAME 	= "FROM Event e LEFT JOIN FETCH e.types AS t WHERE t.name IN (#eventType) AND e.account.username LIKE #username ORDER BY e.#orderBy #orderType";
	public static String FIND_INTERVAL 				= "SELECT i FROM Interval AS i WHERE i.accountEvent.id = :id";
	public static String GET_MESSAGES 				= "FROM Message AS m WHERE m.event.id = :id ORDER BY m.#orderBy #orderType";
	public static String FIND_PARTICIPATION 		= "SELECT p FROM Participation AS p WHERE p.event.id = :id";
	public static String GET_PARTICIPANTS 			= "FROM Participation p ORDER BY p.#orderBy #orderType";
	public static String FIND_TYPE 					= "SELECT et FROM EventType AS et WHERE et.event.id = :id";
	public static String GET_TYPES 					= "FROM EventType AS et WHERE et.event.id = :id";
	// -----------------------------------------------------------------------------------------------------
}
