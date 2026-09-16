namespace SammanCoaching.Phonebook;

public interface IAuthorizer
{
    Task<bool> IsAuthorizedAsync();
}
