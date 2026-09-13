Notes from my studies as I learn syntax and usage of Java.

# Enum
An enum is a FIXED constraint, meaning it cannot be changed.

public enum Peak {
    NEBO, PROVO, SANTAQUIN, TIMPANOGOS}
}

"public" means that this function can be used from anywhere in the program.
The other option is private, which means that it can only be referenced within the same class.
enum is what the function returns. "void" means it returns nothing.
"int[]" means it returns an array of ints.

# Override

To override the previous class that we are inheriting from, type "@Override the line before the class:"
Ex:
@Override
public String toString() {
    return "PetClass[id=" + id + ", name ='" + name + "']"
}
This overrides the java.lang.Object class's definition for toString().

Summary: "public" still means it can be called by other functions. String means it returns a string. @Override means it gets overriden.

# Records

They uphold good practice for objects in that they:
- Are immutable - all fields are private and final.
- It creates "getters" on its own. They are just the name of the fields you pass in.
- equals() a method that compares two records based on their field values.
- hashCode() a method that calculates a hash baed on all fields.
- toString() a method that creates a string representation showing all field names and their values.

record PetRecord(int id, String name, String type) {}

This is all you need to create the record. Saves 50+ lines of code.
You can add custom methods, too, but note that you still cannot edit the immutable code.
Ex:

`record PetRecord(int id, String name, String type) {
    public PetRecord rename(String newName) {
        return new PetRecord(id, newName, type);
    }
}`
Notice that it just returns a new PetRecord, since it is immutable and it cannot change the original.
This would be called by, say, a var tucker = new PetRecord(1, Tucker, Dog) was created.
To rename it would be tucker.rename(Brody)
And it would create a new PetRecord with 
