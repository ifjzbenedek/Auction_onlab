export interface UserBasicDTO {
    id: number
    userName: string       
    emailAddress: string
    phoneNumber: string
  }

  export interface UserCredentialsDTO {
    userName: string
  }
  
  export interface UserRegistrationDTO {
    userName: string
    emailAddress: string
  }