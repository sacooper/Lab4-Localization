/*********
 * The purpose of the Option class is to provide a means
 * of representing the idea of "Some _" or "None" from ML style
 * programming languages (same as Maybe with "Just _" or "Nothing" in Haskell).
 * 
 * @author Scott Cooper
 * 
 * @param <T> Type of the Option being used
 */
public final class Option<T>{
	/*********
	 * Exception thrown when an attempt is made to
	 * get a value from "None"	*/
	public static class InvalidOption extends Exception{}
	
	// Value being stored
	private T value;
	// Whether or not it is empty (necessary, maybe they want to store null)
	private boolean isEmpty;
	
	/*******
	 * Private constructor. Only way to construct a new Option is via
	 * the static methods.
	 * 
	 * @param isEmpty If this is empty (None)
	 * @param value The value to store (
	 */
	private Option (boolean isEmpty, T value) {
		this.value = value;
		this.isEmpty = isEmpty;}
	
	/******
	 * Whether or not this Option is "None"
	 * @return "true" iff this Option is "None"
	 */
	public boolean isNone(){return isEmpty;}
	
	/******
	 * Retrieve the value from an Option
	 * 
	 * @return The value of this option
	 * @throws InvalidOption Exception thrown when 
	 * 		   attempt to retrieve value from None
	 */
	public T get() throws InvalidOption{
		if (isEmpty)
			throw new InvalidOption();
		else
			return value;}

	/******
	 * Retrieve the value from an Option
	 * 
	 * @return The value of this option or null if empty
	 */
	public T getElseNull() {
		if (isEmpty)
			return null;
		else
			return value;}
	
	/******
	 * Retrieve the value from an Option. If this Option is
	 * "None", return the other option
	 * 
	 * @param other What should be returned if this option is None
	 * @return The value of this Option or "other" if this Option is None
	 */
	public T getElse(T other){
		if (isEmpty)
			return other;
		else return value;}
	
	/********
	 * Set this Option to None
	 */
	public void toNone(){
		this.isEmpty = true;
		this.value = null;}
	
	/********
	 * Set this Option to Some(value)
	 * @param value The value to set this Option to.
	 */
	public void toSome(T value){
		this.isEmpty = false;
		this.value = value;}
	
	@Override 
	public boolean equals(Object obj){
		if (obj instanceof Option<?>){
			try {
				Option<?> t = Option.class.cast(obj);
				if (t.isEmpty) return this.isEmpty;
				else {
					return t.get().equals(value);}
			} catch (Exception e) {
				return false;}
		}else return false;}
	
	/*******
	 * Create a new instance of a "none" Option
	 * @return a new "None" of type X
	 */
	public static <X> Option<X> none(){
		return new Option<X>(true, null);}
	
	/******
	 * Create a new instance of a Some v option
	 * @param v The value of this Option
	 * @return a new Some v Option of type X
	 */
	public static <X> Option<X> of(X v){
		return new Option<X>(true, v);}
	
}